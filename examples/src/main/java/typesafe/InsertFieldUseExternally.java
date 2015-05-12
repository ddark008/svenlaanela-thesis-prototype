package typesafe;

import org.zeroturnaround.javassist.annotation.Modify;
import org.zeroturnaround.javassist.annotation.Patches;

import sample.OtherClass;
import sample.OtherClass_Mirror;
import sample.SampleClass;
import sample.SampleClass_Mirror;

@Patches(SampleClass.class)
public class InsertFieldUseExternally extends SampleClass_Mirror {
  public String addedField = "addedField";
}

@Patches(OtherClass.class)
class UseAddedFieldInOtherClass extends OtherClass_Mirror {
  @Modify
  public String otherMethod(String $1) {
    $1 = ((InsertFieldUseExternally) sampleClass).addedField;
    return super.otherMethod($1);
  }
}