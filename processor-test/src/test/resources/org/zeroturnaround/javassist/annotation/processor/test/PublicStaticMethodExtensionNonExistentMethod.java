package org.zeroturnaround.javassist.annotation.processor.test;

import org.zeroturnaround.javassist.annotation.Modify;
import org.zeroturnaround.javassist.annotation.Patches;

import java.lang.Override;

@Patches(PublicStaticMethod.class)
public class PublicStaticMethodExtensionNonExistentMethod extends PublicStaticMethod_Mirror {
  @Modify
  public String nonExistentMethod(String $1) {
    return "Hello world!";
  }
}