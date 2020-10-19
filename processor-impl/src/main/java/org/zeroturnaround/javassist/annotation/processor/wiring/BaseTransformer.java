package org.zeroturnaround.javassist.annotation.processor.wiring;

import javassist.bytecode.Descriptor;
import org.zeroturnaround.javassist.annotation.Modify;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

public class BaseTransformer implements org.zeroturnaround.javassist.annotation.processor.wiring.JavassistClassBytecodeProcessor {
  private static final String EXTENSION_FIELD = "__companion";
  private static final String ORIGINAL_FIELD = "__original";
  private static final String ORIGINAL_METHOD = "__original";
  private static final String ORIGINAL_AWARE_CLASS = "org.zeroturnaround.javassist.annotation.OriginalAware";

  private final String originalClassName;
  private final String originalClassNameJVM; // Original class name in internal representation of the class name in the JVM
  private final String extensionClassName;


  public BaseTransformer(String originalClassName, String originalClassNameJVM, String extensionClassName) {
    this.originalClassName = originalClassName;
    this.originalClassNameJVM = originalClassNameJVM;
    this.extensionClassName = extensionClassName;
  }

  public void process(final ClassPool cp, final ClassLoader cl, final CtClass originalClass) throws Exception {
    originalClass.addField(CtField.make(String.format("final %s %s = new %s ();", extensionClassName, EXTENSION_FIELD, extensionClassName), originalClass));

    CtClass extensionClass = cp.get(extensionClassName);
    extensionClass.addField(CtField.make(String.format("private %s %s = null;", originalClassName, ORIGINAL_FIELD), extensionClass));
    extensionClass.addInterface(cp.get(ORIGINAL_AWARE_CLASS));
    extensionClass.addMethod(CtMethod.make(String.format("public void setOriginal(java.lang.Object original) { %s = (%s) original; }", ORIGINAL_FIELD, originalClassName), extensionClass));

    for (final CtMethod extensionMethod : extensionClass.getDeclaredMethods()) {
      rewireOriginalClassMethodsToCallExtension(originalClass, extensionClass, extensionMethod);

      // The companion class was compiled against a mirror of the original, overwrite all method and field accesses
      // to invoke the original
      rewireExtensionClassMethodsToCallOriginal(extensionMethod, originalClass);
    }

    // Static block of extension is executed after original static block
    CtConstructor extStaticInitializer = extensionClass.getClassInitializer();
    if (extStaticInitializer != null) {
      System.out.println("STATIC INIT: " + extStaticInitializer.toString());
      rewireExtensionClassMethodsToCallOriginal(extStaticInitializer, originalClass);
      CtConstructor origStaticInitializer = originalClass.getClassInitializer();
      if (origStaticInitializer == null) {
        originalClass.makeClassInitializer().setBody(extStaticInitializer, null);
      } else {
        origStaticInitializer.insertAfter(extStaticInitializer.toString());
      }
    }

    for (CtConstructor originalConstructor : originalClass.getDeclaredConstructors()) {
      originalConstructor.insertAfter("{ (("+ORIGINAL_AWARE_CLASS+")"+EXTENSION_FIELD+").setOriginal(this); }");
    }

    for (CtField extensionField : extensionClass.getDeclaredFields()) {
      if (extensionField.hasAnnotation(Modify.class)) {
        CtField originalField = originalClass.getField(extensionField.getName());
        originalField.setModifiers(extensionField.getModifiers());
      } else {

      }
    }

    extensionClass.toClass(cl, null);
  }

  /**
   * Method for rewiring method calls of the original class to method calls of the extension class.
   *
   * For every method in the extension class that *does not exist* in the original class, a method with the same
   * signature is created into the original class. The created original method body is set to delegate to the method
   * in the extension class.
   *
   * For every method in the extension class that *does exist* in the original class, a copy of the original method
   * is created into the original class with a name suffixed with __original. The original method body is set to delegate
   * to the method in the extension class.
   *
   *
   */
  private void rewireOriginalClassMethodsToCallExtension(CtClass originalClass, CtClass extensionClass, CtMethod extensionMethod) throws CannotCompileException, NotFoundException {
    CtMethod originalMethod;
    CtMethod originalCopy;
    try {
      originalMethod = originalClass.getDeclaredMethod(extensionMethod.getName(), extensionMethod.getParameterTypes()); // throws NotFoundException if not a method override!
      // for all methods that are overriden in the extension class, create a copy for the original method
      // and redirect the original method to call the method defined in the companion class (which will call
      // the created copy of the original method if required.
      originalCopy = CtNewMethod.copy(originalMethod, extensionMethod.getName() + ORIGINAL_METHOD, originalClass, null);
      originalClass.addMethod(originalCopy);
    } catch (NotFoundException ignored) { // if a method of the extension class does not exist in the original, add it
      originalMethod = CtNewMethod.make(extensionMethod.getModifiers(), extensionMethod.getReturnType(), extensionMethod.getName(), extensionMethod.getParameterTypes(), extensionMethod.getExceptionTypes(), null, originalClass);
      originalClass.addMethod(originalMethod);
    }
    String target = Modifier.isStatic(extensionMethod.getModifiers()) ? extensionClass.getName() : EXTENSION_FIELD;
    if ("void".equals(originalMethod.getReturnType().getName())) {
      originalMethod.setBody("{ "+target+"."+extensionMethod.getName()+"($$);}");
    } else {
      originalMethod.setBody("{ return "+target+"."+extensionMethod.getName()+"($$);}");
    }
  }

  /**
   * Method for rewiring method calls and field accesses of the extension class to (possibly synthetic) method
   * calls and field accesses on the original class.
   *
   * During compile-time, the extension class extends from a mirror class of the original (suffix _Mirror) which
   * was generated to provide code completion for the developer. The mirror class is pretty much an exact copy of
   * the original class, just with more relaxed access modifiers. During runtime however, this mirror class is not
   * present in the application and method calls and field accesses that target the mirror class have to be updated
   * to target the original class instead.
   *
   * If the method or field that the extension class calls/accesses is private in the original class, a synthetic
   * accessor will be generated into the original class and the extension class will access the private member of
   * the original class via this synthetic accessor.
   */
  private void rewireExtensionClassMethodsToCallOriginal(final CtBehavior extensionClassExecutable, final CtClass originalClass) throws CannotCompileException, NotFoundException {
    extensionClassExecutable.instrument(new ExprEditor() {
      public void edit(final MethodCall m) throws CannotCompileException {
        try {
          if (m.getClassName().equals(originalClass.getName() + "_Mirror")) {
            System.out.println("Rewiring method call in extension: "+m.getClassName()+"."+m.getMethodName()+"() -> "+originalClass.getName()+"."+m.getMethodName()+ORIGINAL_METHOD + "()");
            CtMethod originalMethod = originalClass.getMethod(m.getMethod().getName(), m.getMethod().getSignature());

            // create synthetic accessor for private members. Ignores existing synthetic accessors and always creates a new one.
            if (Modifier.isPrivate(originalMethod.getModifiers())) {
              String syntheticMethodName = createSyntheticAccessor(originalClass, originalMethod);
              if ("void".equals(m.getMethod().getReturnType().getName())) {
                m.replace("{ "+originalClass.getName()+"." + syntheticMethodName +"("+ORIGINAL_FIELD+", $$); }");
              } else {
                m.replace("{ $_ = "+originalClass.getName()+"." + syntheticMethodName +"("+ORIGINAL_FIELD+", $$); }");
              }
            } else {
              String target = Modifier.isStatic(m.getMethod().getModifiers()) ? originalClass.getName() : ORIGINAL_FIELD;
              if ("void".equals(m.getMethod().getReturnType().getName())) {
                m.replace("{ "+target+"." + m.getMethodName() + ORIGINAL_METHOD+"($$); }");
              } else {
                m.replace("{ $_ = "+target+"." + m.getMethodName() + ORIGINAL_METHOD+"($$); }");
              }
            }
          }
        } catch (NotFoundException e) { e.printStackTrace();}
      }
      public void edit(FieldAccess f) throws CannotCompileException {
        try {
          if (f.getField().getDeclaringClass().getName().equals(originalClass.getName() +"_Mirror")) {
        	  /* extract method rewireFieldAccessFromMirrorToOriginal */
            System.out.println("Rewiring field access in extension: "+f.getField().getDeclaringClass().getName()+"."+f.getFieldName()+" -> "+originalClass.getName()+"."+f.getFieldName());
            CtField originalField = originalClass.getDeclaredField(f.getFieldName());

            // create synthetic accessor for private members. Ignores existing synthetic accessors and always creates a new one.
            if (Modifier.isPrivate(originalField.getModifiers())) {
              String syntheticMethodName = createSyntheticAccessor(originalClass, originalField);
              if (f.isReader()) {
                f.replace("{ $_ = "+originalClass.getName()+"."+syntheticMethodName+"("+ORIGINAL_FIELD+")}");
              }
              if (f.isWriter()) {
                f.replace("{ "+originalClass.getName()+"."+syntheticMethodName+"("+ORIGINAL_FIELD+", $1)}");
              }
            } else {
              String target = Modifier.isStatic(f.getField().getModifiers()) ? originalClass.getName() : ORIGINAL_FIELD;
              if (f.isReader()) {
                f.replace("{ $_ = "+target+"." + f.getFieldName() +"; }");
              }
              if (f.isWriter()) {
                f.replace("{"+target+"."+f.getFieldName()+" = $1; }");
              }
            }
          }
        } catch (NotFoundException e) { e.printStackTrace(); }
      }
    });

  }

  private String createSyntheticAccessor(CtClass originalClass, CtField originalField) throws NotFoundException, CannotCompileException {
    String syntheticMethodName = getUniqueSyntheticMethodName();
    System.out.println("Creating synthetic field accessor "+syntheticMethodName+" for: " + originalClass.getName() + "." + originalField.getName());

    String syntheticGetterDeclaration =
      "static " +originalField.getType().getName()+" "+syntheticMethodName+"("+originalClass.getName()+" instance) {"+
        "return instance."+originalField.getName()+";"+
      "}";
    originalClass.addMethod(CtNewMethod.make(syntheticGetterDeclaration, originalClass));

    String syntheticSetterDeclaration =
      "static void "+syntheticMethodName+"("+originalClass.getName()+" instance, "+originalField.getType().getName()+" value) {"+
        "instance."+originalField.getName()+" = value;"+
      "}";
    originalClass.addMethod(CtNewMethod.make(syntheticSetterDeclaration, originalClass));

    return syntheticMethodName;
  }

  private String createSyntheticAccessor(CtClass originalClass, CtMethod originalMethod) throws NotFoundException, CannotCompileException {
    String syntheticMethodName = getUniqueSyntheticMethodName();
    System.out.println("Creating synthetic method "+syntheticMethodName+" for: " + originalClass.getName() + "."+ originalMethod.getSignature());

    String argsString = toArgsString(toClassNames(originalMethod.getParameterTypes()), true);
    String syntheticMethodArgs = originalClass.getName()+ " instance" + (!argsString.isEmpty() ? (", " +argsString) : "");

    String argsStringWithoutTypes = toArgsString(toClassNames(originalMethod.getParameterTypes()), false);
    String syntheticMethodDeclaration =
      "static "+originalMethod.getReturnType().getName()+" "+syntheticMethodName+"("+syntheticMethodArgs+") {"+
        "return instance."+originalMethod.getName()+ORIGINAL_METHOD+"("+argsStringWithoutTypes+");"+
      "}";
    originalClass.addMethod(CtNewMethod.make(syntheticMethodDeclaration, originalClass));

    return syntheticMethodName;
  }

  private String[] toClassNames(CtClass[] ctClasses) {
    String[] result = new String[ctClasses.length];
    for (int i = 0; i < ctClasses.length; i++) {
      result[i] = ctClasses[i].getName();
    }
    return result;
  }

  private String toArgsString(String[] argClasses, boolean includeTypes) {
    if (argClasses == null || argClasses.length == 0) {
      return "";
    } else {
      String result = "";
      boolean addComma = false;
      for (int i = 0; i < argClasses.length; i++) {
        result += addComma ? ", " : "";
        result += includeTypes ? argClasses[i] + " " : "";
        result += "$synarg"+(i+1);
        addComma = true;
      }
      return result;
    }
  }

  // should be good enough for avoiding collisions
  private int syntheticMethodCounter = 9999;
  private String getUniqueSyntheticMethodName() {
    return "access$" + syntheticMethodCounter--;
  }

  public String getOriginalClassNameJVM() {
    return originalClassNameJVM;
  }
}