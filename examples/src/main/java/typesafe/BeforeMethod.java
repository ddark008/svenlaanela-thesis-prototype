package typesafe;

import org.zeroturnaround.javassist.annotation.Patches;

import sample.SampleClass;

@Patches(SampleClass.class)
public class BeforeMethod extends SampleClass_Mirror {
  @Override
  String instanceMethod(String $1) {
    if ($1 == null) {
      return null;
    }
    return super.instanceMethod($1);
  }
}
