package typesafe;

import org.zeroturnaround.javassist.annotation.Patches;

import sample.OtherClass;
import sample.SampleClass;

@Patches(SampleClass.class)
public class InsertMethodUseExternally extends SampleClass_Mirror {
  public String trim(String input) {
    if (input == null) {
      return null;
    }
    return input.trim();
  }
}

@Patches(OtherClass.class)
class UseAddedMethodInOtherClass extends OtherClass_Mirror {
  @Override
  String otherMethod(String $1) {
    Object sampleClass = new SampleClass();
    $1 = ((InsertMethodUseExternally) sampleClass).trim($1);
    return super.otherMethod($1);
  }
}