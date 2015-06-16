package sample;

import org.zeroturnaround.javassist.annotation.Modify;
import org.zeroturnaround.javassist.annotation.Patches;

@Patches(SampleClass.class)
public class SampleClassExtension2
extends SampleClass_Mirror {
  @Modify
  public String greet(String name) {
    if (name == null) {
      return null;
    }
    return super.instanceMethod(name);
  }
}