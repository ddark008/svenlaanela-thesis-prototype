package sample;

import org.zeroturnaround.javassist.annotation.Patches;

@Patches(SampleClass.class)
public class SampleClassExtension2 extends SampleClassMirror {
  @Override
  protected String instanceMethod(String input) {
    anotherMethod(input);
    return super.instanceMethod(input);
  }
}
