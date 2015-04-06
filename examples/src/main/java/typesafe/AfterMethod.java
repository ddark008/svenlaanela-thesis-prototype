package typesafe;

import org.zeroturnaround.javassist.annotation.Patches;

import sample.SampleClass;

@Patches(SampleClass.class)
public class AfterMethod extends SampleClass_Mirror {
  @Override
  String instanceMethod(String input) {
    return super.instanceMethod(input) + "!";
  }
}