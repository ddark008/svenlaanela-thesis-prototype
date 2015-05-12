package typesafe;

import org.zeroturnaround.javassist.annotation.Modify;
import org.zeroturnaround.javassist.annotation.Patches;

import sample.SampleClass;
import sample.SampleClass_Mirror;

@Patches(SampleClass.class)
public class UpdateFieldAndMethodModifiers extends SampleClass_Mirror {
  @Modify
  public String instanceField; 
  
  @Modify
  protected synchronized String instanceMethod(String $1) {
    return super.instanceMethod($1);
  }
}