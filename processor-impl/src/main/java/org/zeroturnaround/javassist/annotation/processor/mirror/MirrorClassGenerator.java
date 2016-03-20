package org.zeroturnaround.javassist.annotation.processor.mirror;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.javassist.annotation.MethodCall;

import javassist.*;
import javassist.bytecode.AccessFlag;

/**
 * MirrorClass encapsulates the logic of generating a mirror class (and required hierarchy) for a given original class.
 *
 */
public class MirrorClassGenerator {
  private static final Logger logger = LoggerFactory.getLogger(MirrorClassGenerator.class);
  private final String originalClassName;
  private final ClassPool classPool;
  
  public MirrorClassGenerator(ClassPool classPool, String originalClassName) {
    this.classPool = classPool;
    this.originalClassName = originalClassName;
  }
  
  public String getOriginalClassName() {
    return originalClassName;
  }
  
  public String getName() {
    // TODO: Mirror classes could use different suffixes?
    return originalClassName + "_Mirror";
  }
  
  public String getSimpleName() {
    return getName().substring(getName().lastIndexOf('.'));
  }

  public String generateSource() throws Exception {
    return generateSource(originalClassName);
  }
  
  private String generateSource(String originalClassName) throws Exception {
    CtClass originalClass = classPool.get(originalClassName);
    return generateSource(originalClass);
  }
  
  private String generateSource(CtClass originalClass) throws Exception {
    StringBuilder result = new StringBuilder();
    result.append("package " + originalClass.getPackageName() + ";\n");
    result.append("\n");
    result.append(generateBodySource(originalClass));
    return result.toString();
  }

  private String generateBodySource(CtClass ctClass) throws Exception {
    StringBuilder result = new StringBuilder();
    
    String mirrorClassName = ctClass.getSimpleName() + "_Mirror";
    if (mirrorClassName.contains("$")) {
      mirrorClassName = mirrorClassName.substring(mirrorClassName.lastIndexOf('$') + 1);
    }

    // add class declaration
    {
      int modifiers = ctClass.getModifiers();
      if (Modifier.isPrivate(modifiers) || Modifier.isPackage(modifiers)) {
        modifiers = Modifier.setPublic(modifiers);
      }
      modifiers = Modifier.clear(modifiers, Modifier.FINAL);
      if (Modifier.isAbstract(modifiers)) {
        modifiers = Modifier.clear(modifiers, Modifier.ABSTRACT);
      }
      
      if (!Modifier.isInterface(modifiers)) {
        result.append(Modifier.toString(modifiers) + " class " + mirrorClassName); // public class className
      } else {
        result.append(Modifier.toString(modifiers) + " " + mirrorClassName);
      }
      CtClass superClass = ctClass.getSuperclass();
      if (superClass != null && !"java.lang.Object".equals(superClass.getName())) {
        String superClassName = superClass.getName().replace("$", ".");
        result.append(" extends " + superClassName); // extends parentClassName
      }
      CtClass[] interfaceClasses = ctClass.getInterfaces();
      if (interfaceClasses.length > 0) {
        result.append(" implements ");
        for (CtClass interfaceClass : interfaceClasses) {
          result.append(interfaceClass.getName().replace("$", "."));
        }
      }
      
      result.append(" {\n");

      if (!Modifier.isInterface(modifiers)) {
        result.append("protected final void instrument(" + MethodCall.class.getName() + " call) {}\n");
        
        // always add default constructor unless we are mirroring an interface!
        logger.debug("Adding default constructor for " + mirrorClassName);
        String s = "public " + mirrorClassName + "() {";
        // find eligible super constructor
        for (CtConstructor superConstructor : ctClass.getSuperclass().getDeclaredConstructors()) {
          if (Modifier.isPublic(superConstructor.getModifiers()) 
              || Modifier.isProtected(superConstructor.getModifiers()) 
              || Modifier.isPackage(superConstructor.getModifiers()) 
                 && ctClass.getSuperclass().getPackageName().equals(ctClass.getPackageName())) {
            s += " super(";
            for (int i = 0; i < superConstructor.getParameterTypes().length; i++) {
              if (i != 0)
                s += ", ";
              s += "null";
            }
            s += "); ";
            break;
          }
        }
        s += "}\n";
        result.append(s);
      }
    }

    // add nested classes
    for (CtClass nestedClass : ctClass.getDeclaredClasses()) {
      String innerClassName = nestedClass.getName().substring(ctClass.getName().length() + 1);
      logger.debug("Nested class: " + innerClassName);
      try {
        Integer.parseInt(innerClassName); // anonymous inner class
      }
      catch (NumberFormatException e) {
        String nestedClassSrc = generateBodySource(nestedClass);
        result.append(nestedClassSrc);
      }
    }
    
    // add constructors, convert to public for access/override
    for (CtConstructor constructor : ctClass.getDeclaredConstructors()) {
      if (constructor.getParameterTypes().length == 0) {
        continue; // do not add default constructor again
      }
      logger.debug("Adding constructor for " + mirrorClassName + " " + constructor.getModifiers() + " " + Modifier.toString(constructor.getModifiers()));
      if ((constructor.getModifiers() & AccessFlag.SYNTHETIC) != 0 || constructor.getModifiers() == 0) { // wtf
        logger.debug("Constructor is synthetic");
        continue;
      }
      String s = "public " + mirrorClassName + "(";
      for (int i = 0; i < constructor.getParameterTypes().length; i++) {
        if (i != 0)
          s += ", ";
        CtClass parameter = constructor.getParameterTypes()[i];
        String parameterName = toMirrorSafeName(ctClass, parameter);
        s += parameterName + " $" + (i + 1);
      }
      s += ") {";

      // find eligible super constructor
      for (CtConstructor superConstructor : ctClass.getSuperclass().getDeclaredConstructors()) {
        if (Modifier.isPublic(superConstructor.getModifiers()) 
            || Modifier.isProtected(superConstructor.getModifiers()) 
            || Modifier.isPackage(superConstructor.getModifiers()) 
               && ctClass.getSuperclass().getPackageName().equals(ctClass.getPackageName())) {
          s += "  super(";
          for (int i = 0; i < superConstructor.getParameterTypes().length; i++) {
            if (i != 0)
              s += ", ";
            s += "null";
          }
          s += ");";
          break;
        }
      }

      s += "}\n";
      result.append(s);
    }

    // convert all class fields to public for access
    for (CtField field : ctClass.getDeclaredFields()) {
      int modifiers = field.getModifiers();
      if (Modifier.isPrivate(modifiers) || Modifier.isPackage(modifiers)) {
        modifiers = Modifier.setProtected(modifiers);
      }
      modifiers = Modifier.clear(modifiers, Modifier.FINAL);

      String typeName = toMirrorSafeName(ctClass, field.getType());

      result.append(Modifier.toString(modifiers) + " " + typeName + " " + field.getName() + " = " + getDefaultValue(field.getType()) + ";\n");
    }

    // convert all methods to public non-final for access/override
    for (CtMethod method : ctClass.getDeclaredMethods()) {
      logger.debug("Adding method for " + mirrorClassName + " " + method.getModifiers() + " " + Modifier.toString(method.getModifiers()));
      logger.debug("Checking for synthetic");
      if ((method.getModifiers() & AccessFlag.SYNTHETIC) != 0 || method.getName().startsWith("access$")) { // wtf
        logger.debug("is synthetic");
        continue;
      }
      String methodSrc = addMethod(ctClass, mirrorClassName, method);
      result.append(methodSrc);
    }

    result.append("}\n");
    return result.toString();
  }
    

  private String addMethod(final CtClass ctClass, final String mirrorClassName, final CtMethod method) throws NotFoundException, CannotCompileException {
    StringBuilder result = new StringBuilder();
    int modifiers = method.getModifiers();
    if (Modifier.isPrivate(modifiers) || Modifier.isPackage(modifiers)) {
      modifiers = Modifier.setProtected(modifiers);
    }
    modifiers = Modifier.clear(modifiers, Modifier.FINAL);

    String returnType = toMirrorSafeName(ctClass, method.getReturnType());
    result.append(Modifier.toString(modifiers) + " " + returnType + " " + method.getName() + "(");
    for (int i = 0; i < method.getParameterTypes().length; i++) {
      CtClass parameter = method.getParameterTypes()[i];
      if (i != 0)
        result.append(", ");
      String parameterName = toMirrorSafeName(ctClass, parameter);
      result.append(parameterName + " $" + (i + 1));
    }
    result.append(")");
    
    if (Modifier.isAbstract(modifiers)) {
      result.append(";\n");
    }
    else {
      result.append(" {");
      String defaultValue = getDefaultValue(method.getReturnType());
      if (defaultValue != null) {
        result.append(" return " + defaultValue + "; ");
      }
      result.append("}\n");
    }
    return result.toString();
  }
    
  private String toMirrorSafeName(CtClass containingClass, CtClass type) throws NotFoundException {
    if (Arrays.asList(containingClass.getDeclaredClasses()).contains(type)) {
      return type.getName().substring(type.getName().lastIndexOf('$') + 1) + "_Mirror";
    }
    else {
      return type.getName();
    }
  }
    
  private String getDefaultValue(CtClass clazz) {
    String name = clazz.getName();
    if ("void".equals(name)) return null;
    else if ("char".equals(name)) return "'x'";
    else if ("short".equals(name)) return "0";
    else if ("int".equals(name)) return "0";
    else if ("long".equals(name)) return "0";
    else if ("boolean".equals(name)) return "false";
    else if ("float".equals(name)) return "0.0";
    else if ("double".equals(name)) return "0.0";
    else return "null";
  }

}
