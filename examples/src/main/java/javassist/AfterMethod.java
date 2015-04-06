package javassist;

import sample.SampleClass;

public class AfterMethod extends JavassistHelper {
  
  public void addSuffixToInstanceMethodReturnValue() throws Exception {
    CtMethod ctMethod = JavassistHelper.getMethod(SampleClass.class, "instanceMethod", String.class);
    ctMethod.insertAfter("$_ = $_ + \"!\";");
  }

}
