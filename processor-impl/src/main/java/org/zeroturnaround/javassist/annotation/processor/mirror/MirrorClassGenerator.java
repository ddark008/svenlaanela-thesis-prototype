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
  private static final String EXTENSION = "_Mirror"; // TODO: Mirror classes could use different suffixes?
  private static final Logger logger = LoggerFactory.getLogger(MirrorClassGenerator.class);
  private final CtClass originalClass;

  public MirrorClassGenerator(CtClass originalClass) {
    this.originalClass = originalClass;
  }
  
  public String getName() {
    return originalClass.getName() + EXTENSION;
  }

  public String generateSource() throws Exception {
    return generateSource(originalClass);
  }
  
  private String generateSource(CtClass originalClass) throws Exception {
    StringBuilder result = new StringBuilder();
    result.append("package " + originalClass.getPackageName() + ";\n");
    result.append("\n");
    result.append(toClassString(originalClass));
    return result.toString();
  }

  private String toClassString(CtClass ctClass) throws Exception {
    StringBuilder result = new StringBuilder();
    
    String mirrorClassName = ctClass.getSimpleName() + EXTENSION;
    if (mirrorClassName.contains("$")) {
      mirrorClassName = mirrorClassName.substring(mirrorClassName.lastIndexOf('$') + 1);
    }

    // add class declaration
    int modifiers = ctClass.getModifiers();
    {
      if (Modifier.isPrivate(modifiers) || Modifier.isPackage(modifiers)) {
        modifiers = Modifier.setPublic(modifiers);
      }
      modifiers = Modifier.clear(modifiers, Modifier.FINAL);

      if (Modifier.isAbstract(modifiers)) {
        modifiers = Modifier.clear(modifiers, Modifier.ABSTRACT);
      }

      if (Modifier.isInterface(modifiers)) {
        result.append(Modifier.toString(modifiers) + " " + mirrorClassName);
      } else {
        result.append(Modifier.toString(modifiers) + " class " + mirrorClassName);
      }

      // add extends clause
      CtClass superClass = ctClass.getSuperclass();
      if (superClass != null && !"java.lang.Object".equals(superClass.getName())) {
        String superClassName = superClass.getName().replace("$", ".");
        result.append(" extends " + superClassName);
      }

      // add implements clause
      CtClass[] interfaceClasses = ctClass.getInterfaces();
      if (interfaceClasses.length > 0) {
        result.append(" implements ");
        for (int i = 0; i < interfaceClasses.length; i++) {
          String interfaceName = interfaceClasses[i].getName().replace("$", ".");
          result.append((i == 0) ? interfaceName : ", " + interfaceName);
        }
      }
      result.append(" {\n");


      if (!Modifier.isInterface(modifiers)) {
        result.append("protected final void instrument(" + MethodCall.class.getName() + " call) {}\n");
      }
    }

    // add constructors, convert to public for access/override
    if (!Modifier.isInterface(modifiers)){
      // always add default constructor unless we are mirroring an interface!
      logger.debug("Adding default constructor for " + mirrorClassName);
      result.append("public " + mirrorClassName + "() {");

      // find eligible super constructor
      for (CtConstructor superConstructor : ctClass.getSuperclass().getDeclaredConstructors()) {
        if (Modifier.isPublic(superConstructor.getModifiers())
            || Modifier.isProtected(superConstructor.getModifiers())
            || Modifier.isPackage(superConstructor.getModifiers())
            && ctClass.getSuperclass().getPackageName().equals(ctClass.getPackageName())) {
          result.append(toConstructorInvocationString(superConstructor));
          break;
        }
      }
      result.append("}\n");

      for (CtConstructor constructor : ctClass.getDeclaredConstructors()) {
        if (constructor.getParameterTypes().length == 0) {
          continue; // do not add default constructor again
        }
        logger.debug("Adding constructor for " + mirrorClassName + " " + constructor.getModifiers() + " " + Modifier.toString(constructor.getModifiers()));

        if ((constructor.getModifiers() & AccessFlag.SYNTHETIC) != 0 || constructor.getModifiers() == 0) { // wtf
          logger.debug("Constructor is synthetic");
          continue;
        }

        result.append("public " + mirrorClassName + "(" + toParameterString(constructor) + ") ");
        if (constructor.getExceptionTypes().length > 0) {
          result.append("throws " + toExceptionString(constructor.getExceptionTypes()) + " ");
        }
        result.append("{ "+mirrorClassName+"(); }\n");
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
        result.append(toClassString(nestedClass));
      }
    }


    // add fields, convert to protected non-final for access
    for (CtField field : ctClass.getDeclaredFields()) {
      int fieldModifiers = stripFinalPublicPackage(field.getModifiers());
      String typeName = toMirrorSafeName(ctClass, field.getType());

      result.append(Modifier.toString(fieldModifiers) + " " + typeName + " " + field.getName() + " = " + getDefaultValue(field.getType()) + ";\n");
    }

    // add methods, convert to protected non-final for access
    for (CtMethod method : ctClass.getDeclaredMethods()) {
      logger.debug("Adding method for " + mirrorClassName + " " + method.getModifiers() + " " + Modifier.toString(method.getModifiers()));
      if (isSynthetic(method)) {
        continue;
      }
      result.append(toMethodString(ctClass, method));
    }

    result.append("}\n");
    return result.toString();
  }

  private int stripFinalPublicPackage(int modifiers) {
    if (Modifier.isPrivate(modifiers) || Modifier.isPackage(modifiers)) {
      modifiers = Modifier.setProtected(modifiers);
    }
    return Modifier.clear(modifiers, Modifier.FINAL);
  }

  private boolean isSynthetic(CtMethod ctMethod) {
    if ((ctMethod.getModifiers() & AccessFlag.SYNTHETIC) != 0 || ctMethod.getName().startsWith("access$")) { // wtf
      logger.debug(ctMethod.toString() + "is synthetic");
      return true;
    } else {
      return false;
    }
  }

  private String toMethodString(final CtClass ctClass, final CtMethod ctMethod) throws NotFoundException, CannotCompileException {
    StringBuilder result = new StringBuilder();

    int modifiers = stripFinalPublicPackage(ctMethod.getModifiers());

    String returnType = toMirrorSafeName(ctClass, ctMethod.getReturnType());
    result.append(Modifier.toString(modifiers) + " " + returnType + " " + ctMethod.getName() + "(");

    result.append(toParameterString(ctMethod));
    result.append(")");
    if (ctMethod.getExceptionTypes().length > 0) {
      result.append(" throws " + toExceptionString(ctMethod.getExceptionTypes()));
    }
    result.append("{");

    String defaultValue = getDefaultValue(ctMethod.getReturnType());
    if (defaultValue != null) {
      result.append(" return " + defaultValue + "; ");
    }
    result.append("}\n");
    return result.toString();
  }

  /**
   * TODO: Should probably add class CtHelper for these kinds of methods?
   *
   * Generates a method parameter list string for the mirror class. Substitutes all references to classes that have mirrors to their mirror class names
   */
  private String toParameterString(CtBehavior ctBehavior) throws NotFoundException {
    String result = "";
    for (int i = 0; i < ctBehavior.getParameterTypes().length; i++) {
      String parameterType = toMirrorSafeName(ctBehavior.getDeclaringClass(), ctBehavior.getParameterTypes()[i]);
      String parameter = parameterType + " $" + (i+1);
      result += (i == 0) ? parameter : ", " + parameter;
    }
    return result;
  }

  private String toExceptionString(CtClass[] ctClasses) throws NotFoundException {
    String result = "";
    for (int i = 0; i < ctClasses.length; i++) {
      String className = ctClasses[i].getName();
      result += (i == 0) ? className : ", " + className;
    }
    return result;
  }
    
  private String toMirrorSafeName(CtClass containingClass, CtClass type) throws NotFoundException {
    if (Arrays.asList(containingClass.getDeclaredClasses()).contains(type)) {
      return type.getName().substring(type.getName().lastIndexOf('$') + 1) + "_Mirror";
    }
    else {
      return type.getName();
    }
  }

  /**
   * TODO: Should probably add class CtHelper for these kinds of methods?
   *
   * Generate an invocation string for a particular super constructor in the form of
   * super(null, 0, 0.0, false, 'x');
   *
   * @return
   */
  private String toConstructorInvocationString(CtConstructor ctConstructor) throws NotFoundException {
    String result = "super(";
    for (int i = 0; i < ctConstructor.getParameterTypes().length; i++) {
      String defaultValue = getDefaultValue(ctConstructor.getParameterTypes()[i]);
      result += (i == 0) ? defaultValue : ", " + defaultValue;
    }
    result += ");";
    return result;
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