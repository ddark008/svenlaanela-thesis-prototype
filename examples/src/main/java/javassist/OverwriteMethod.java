package javassist;

import sample.SampleClass;

public class OverwriteMethod {
  public void overwriteMethod() throws Exception {
    CtMethod method = JavassistHelper.getMethod(SampleClass.class, "instanceMethod", String.class);
    method.setBody("return \"Hello world!\";");
  }
  
}
