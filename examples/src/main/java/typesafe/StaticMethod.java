package typesafe;

import org.zeroturnaround.javassist.annotation.Modify;
import org.zeroturnaround.javassist.annotation.Patches;

import sample.SampleClass_Mirror;
import sample.StaticClass;

@Patches(StaticClass.class)
public class StaticMethod extends SampleClass_Mirror {
  @Modify
  public static String staticMethod(String $1) {
    return SampleClass_Mirror.staticMethod($1) + "!";
  }
}
