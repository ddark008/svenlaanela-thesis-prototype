package typesafe;

import org.zeroturnaround.javassist.annotation.Modify;
import org.zeroturnaround.javassist.annotation.Patches;

import sample.SampleClass;
import sample.SampleClass_Mirror;

@Patches(SampleClass.class)
public class BadMethodName extends SampleClass_Mirror {
  @Modify
  public String greet(String name) {
    if (name == null) {
      return null
    }
    return super.greet(name);
  }
}


