package typesafe;

import org.zeroturnaround.javassist.annotation.Patches;

import sample.SampleClass;

@Patches(SampleClass.class)
public class InsertFieldUseLocally extends SampleClass_Mirror {
  private String addedField = "addedField";

  @Override
  String instanceMethod(String $1) {
    $1 = $1 + addedField;
    return super.instanceMethod($1);
  }
}