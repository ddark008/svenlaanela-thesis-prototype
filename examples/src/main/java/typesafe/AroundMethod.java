package typesafe;

import org.zeroturnaround.javassist.annotation.Modify;
import org.zeroturnaround.javassist.annotation.Patches;

import sample.SampleClass;
import sample.SampleClass_Mirror;

@Patches(SampleClass.class)
public class AroundMethod extends SampleClass_Mirror {
  @Modify
  protected String instanceMethod(String $1) {
    try {
      return super.instanceMethod($1);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}