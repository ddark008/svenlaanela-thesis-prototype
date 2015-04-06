package typesafe;

import org.zeroturnaround.javassist.annotation.Patches;

import sample.SampleClass;

@Patches(SampleClass.class)
public class OverwriteMethod extends SampleClass_Mirror {
  @Override
  String instanceMethod(String input) {
    return "Hello world!";
  }
}