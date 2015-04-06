package javassist;

import sample.SampleClass;

public class BeforeMethodBad {
  
  public void addNullCheckToInstanceMethod() throws Exception {
    CtMethod ctMethod = JavassistHelper.getMethod(SampleClass.class, "instanceMethod", String.class);
    ctMethod.insertBefore(
      "if ($1 == null) {"+
      "  return null"+
      "}");
  }

}