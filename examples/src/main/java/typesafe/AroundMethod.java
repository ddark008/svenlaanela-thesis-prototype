package typesafe;

import org.zeroturnaround.javassist.annotation.Patches;

import sample.SampleClass;

@Patches(SampleClass.class)
public class AroundMethod extends SampleClass_Mirror {
  @Override
  String instanceMethod(String $1) {
    try {
      return super.instanceMethod($1);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}