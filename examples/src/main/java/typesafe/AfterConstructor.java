package typesafe;

import org.zeroturnaround.javassist.annotation.Modify;
import org.zeroturnaround.javassist.annotation.Patches;

import sample.SampleClass;

@Patches(SampleClass.class)
public class AfterConstructor extends SampleClass_Mirror {
  
  @Modify
  public AfterConstructor() {
    super();
    System.out.println("Constructing");
  }
}
