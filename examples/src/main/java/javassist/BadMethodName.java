package javassist;

import sample.SampleClass;

public class BadMethodName {
  public void replaceMethodBody() throws Exception {
    CtClass ctClass = JavassistHelper.getClass(SampleClass.class);
    CtClass[] args = JavassistHelper.getClasses(String.class.getName());
    CtMethod ctMethod = ctClass.getDeclaredMethod("nstanceMethod", args);
    ctMethod.setBody("return \"Hello world!\";");
  } 
}