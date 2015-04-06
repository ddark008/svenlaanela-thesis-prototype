package javassist;

import sample.SampleClass;

public class AroundMethod {
  public void addTryCatchToMethodCall() throws Exception {
    CtClass ctClass = JavassistHelper.getClass(SampleClass.class);
    CtMethod ctMethod = JavassistHelper.getMethod(SampleClass.class, "instanceMethod", String.class);
    ctClass.addMethod(CtNewMethod.copy(ctMethod, "instanceMethod_copy", ctClass, null));
    ctMethod.setBody(
        "try {"+
        "  instanceMethod_copy($$);"+
        "} catch (Exception e) {"+
        "  e.printStackTrace();"+
        "  return null;"+
        "}");
  }
}
