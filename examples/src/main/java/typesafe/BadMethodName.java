package typesafe;

import org.zeroturnaround.javassist.annotation.Modify;
import org.zeroturnaround.javassist.annotation.Patches;

import sample.SampleClass;
import sample.SampleClass_Mirror;

@Patches(SampleClass.class)
public class BadMethodName extends SampleClass_Mirror {
  @Override
  protected String nstanceMethod(String input) {
    return "Hello world!";
  }
}
