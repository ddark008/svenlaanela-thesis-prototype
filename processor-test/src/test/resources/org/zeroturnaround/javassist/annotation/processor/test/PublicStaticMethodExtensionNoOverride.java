package org.zeroturnaround.javassist.annotation.processor.test;

import org.zeroturnaround.javassist.annotation.Patches;

@Patches(PublicStaticMethod.class)
public class PublicStaticMethodExtensionNoOverride extends PublicStaticMethod_Mirror {
  public String method(String $1) {
    return "Hello world!";
  }
}