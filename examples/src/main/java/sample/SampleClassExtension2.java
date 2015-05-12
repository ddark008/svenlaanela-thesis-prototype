package sample;

import org.zeroturnaround.javassist.annotation.Patches;

@Patches(SampleClass.class)
public class SampleClassExtension2 extends SampleClass_Mirror {
  @Override
  protected String instanceMethod(String input) {
    if (input == null) {
      return null;
    }
    return super.instanceMethod(input);
  }
}
