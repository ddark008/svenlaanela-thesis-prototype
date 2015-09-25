package org.zeroturnaround.javassist.annotation.processor.test;

import org.zeroturnaround.javassist.annotation.Patches;

@Patches(PublicInstanceMethod.class)
public class PublicInstanceMethodExtensionNoOverride extends PublicInstanceMethod_Mirror {
  public String method(String $1) {
    return "Hello world!";
  }
}