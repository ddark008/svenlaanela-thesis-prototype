package typesafe;

import org.zeroturnaround.javassist.annotation.Modify;
import org.zeroturnaround.javassist.annotation.Patches;

import sample.SampleClass;
import sample.SampleClass_Mirror;

@Patches(SampleClass.class)
public class OverwriteField extends SampleClass_Mirror {
  @Modify
  String instanceField = "updatedField";
}
