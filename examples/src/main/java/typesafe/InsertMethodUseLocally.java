package typesafe;

import org.zeroturnaround.javassist.annotation.Patches;

import sample.SampleClass;

@Patches(SampleClass.class)
public class InsertMethodUseLocally extends SampleClass_Mirror {
  public String trim(String input) {
    if (input == null) {
      return null;
    }
    return input.trim();
  }

  @Override
  String instanceMethod(String $1) {
    $1 = trim($1);
    return super.instanceMethod($1);
  }
}