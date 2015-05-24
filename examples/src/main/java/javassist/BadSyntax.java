package javassist;

import sample.SampleClass;

public class BadSyntax {
  public void replaceMethodBody() throws Exception {
    CtClass ctClass = JavassistHelper.getClass(SampleClass.class);
    CtClass[] args = JavassistHelper.getClasses(String.class.getName());
    CtMethod ctMethod = ctClass.getDeclaredMethod("instanceMethod", args);
    ctMethod.setBody("return \"Hello world!\"");
  } 
}