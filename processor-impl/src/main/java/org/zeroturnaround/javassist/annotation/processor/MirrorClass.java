package org.zeroturnaround.javassist.annotation.processor;

import java.util.Arrays;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;

import org.zeroturnaround.javassist.annotation.MethodCall;

/**
 * MirrorClass encapsulates the logic of generating a mirror class (and required hierarchy) for a given original class.
 *
 */
public class MirrorClass {
  private final String originalClassName;
  
  public MirrorClass(String originalClassName) {
    this.originalClassName = originalClassName;
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
    ClassPool classPool = ClassPool.getDefault();
    classPool.insertClassPath(new ClassClassPath(this.getClass()));
    
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
    System.out.println("Generating mirror for class: " + ctClass.getName());
    
    String mirrorClassName = ctClass.getSimpleName() + "_Mirror";
    if (mirrorClassName.contains("$")) {
      mirrorClassName = mirrorClassName.substring(mirrorClassName.lastIndexOf('$') + 1);
    }

    // add class declaration
    {
      int modifiers = ctClass.getModifiers();
      if (Modifier.isPrivate(modifiers) || Modifier.isPackage(modifiers)) {
        modifiers = Modifier.setProtected(modifiers);
      }
      modifiers = Modifier.clear(modifiers, Modifier.FINAL);
      if (Modifier.isAbstract(modifiers)) {
        modifiers = Modifier.clear(modifiers, Modifier.ABSTRACT);
      }

      result.append(Modifier.toString(modifiers) + " class " + mirrorClassName); // public class className
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
        result.append("  protected final void instrument(" + MethodCall.class.getName() + " call) {}\n");
      }
    }

    // add nested classes
    for (CtClass nestedClass : ctClass.getDeclaredClasses()) {
      String innerClassName = nestedClass.getName().substring(ctClass.getName().length() + 1);
      System.out.println("Nested class: " + innerClassName);
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
      System.out.println("Adding constructor for " + mirrorClassName + " " + constructor.getModifiers() + " " + Modifier.toString(constructor.getModifiers()));
      System.out.println("Checking for synthetic");
      if ((constructor.getModifiers() & AccessFlag.SYNTHETIC) != 0 || constructor.getModifiers() == 0) { // wtf
        System.out.println("is synthetic");
        continue;
      }
      String s = "public " + mirrorClassName + "(\n";
      for (int i = 0; i < constructor.getParameterTypes().length; i++) {
        int modifiers = constructor.getModifiers();
        if (Modifier.isPrivate(modifiers) || Modifier.isPackage(modifiers)) {
          modifiers = Modifier.setPublic(modifiers);
        }

        CtClass parameter = constructor.getParameterTypes()[i];
        if (i != 0)
          s += ", ";
        String parameterName = toMirrorSafeName(ctClass, parameter);
        s += parameterName + " $" + (i + 1);
      }
      s += ") {";

      // find eligible super constructor
      for (CtConstructor superConstructor : ctClass.getSuperclass().getDeclaredConstructors()) {
        if (Modifier.isPublic(superConstructor.getModifiers())) {
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
        modifiers = Modifier.setPublic(modifiers);
      }
      modifiers = Modifier.clear(modifiers, Modifier.FINAL);

      String typeName = toMirrorSafeName(ctClass, field.getType());

      result.append(Modifier.toString(modifiers) + " " + typeName + " " + field.getName() + " = " + getDefaultValue(field.getType()) + ";\n");
    }

    // convert all methods to public non-final for access/override
    for (CtMethod method : ctClass.getDeclaredMethods()) {
      System.out.println("Adding method for " + mirrorClassName + " " + method.getModifiers() + " " + Modifier.toString(method.getModifiers()));
      System.out.println("Checking for synthetic");
      if ((method.getModifiers() & AccessFlag.SYNTHETIC) != 0 || method.getName().startsWith("access$")) { // wtf
        System.out.println("is synthetic");
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
      result.append("{\n");
      String defaultValue = getDefaultValue(method.getReturnType());
      if (defaultValue != null) {
        result.append("return " + defaultValue + ";\n");
      }
      result.append("}");
    }
    return result.toString();
    
    /* private methods */
    
    /* Method edit */
//  for (final Element subElement : element.getEnclosedElements()) {
//      if (subElement.getKind() == ElementKind.METHOD || subElement.getKind() == ElementKind.CONSTRUCTOR) {
//          final Edit annotation = subElement.getAnnotation(Edit.class);
//          if (annotation != null) {
//              System.out.println("FOUND ANNOTATION!");
//              method.instrument(new ExprEditor() {
//                  public void edit(MethodCall m) throws CannotCompileException {
//                      try {
//                          Class<?> value = annotation.type();
//                      } catch (MirroredTypeException e) {
//                          TypeMirror annotationTypeMirror = e.getTypeMirror();
//                          System.out.println("ANNOTATION type: " + annotationTypeMirror.toString() + ", METHOD: " + annotation.method());
//                          System.out.println("METHOD type: " + m.getClassName() + ", METHOD: " + m.getMethodName());
//                          if (m.getClassName().equals(annotationTypeMirror.toString()) && m.getMethodName().equals(annotation.method())) {
//                              System.out.println("MATCHES!");
//                              try {
//                                  addMethod(subElement, ctClass, w, mirrorClassName, m.getMethod());
//                              } catch (NotFoundException nfe) {
//                                  nfe.printStackTrace();
//                              }
//                          }
//                      }
//                  }
//              });
//          }
//      }
//  }

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
