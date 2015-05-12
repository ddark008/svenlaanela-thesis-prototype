package typesafe;

import org.zeroturnaround.javassist.annotation.Modify;
import org.zeroturnaround.javassist.annotation.Patches;

import sample.ConstructorClass;
import sample.ConstructorClass_Mirror;

@Patches(ConstructorClass.class)
public class AfterConstructor extends ConstructorClass_Mirror {
  @Modify
  public AfterConstructor(String arg) {
    super(arg);
    System.out.println("Constructing");
  }
}