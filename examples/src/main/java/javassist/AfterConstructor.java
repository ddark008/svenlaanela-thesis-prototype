package javassist;

import sample.SampleClass;

public class AfterConstructor {
  public void afterConstructor() throws Exception {
    CtClass ctClass = JavassistHelper.getClass(SampleClass.class);
    CtConstructor ctConstructor = ctClass.getDeclaredConstructor(new CtClass[]{});
    ctConstructor.insertAfter("System.out.println(\"Constructing!\");");
  }
}
