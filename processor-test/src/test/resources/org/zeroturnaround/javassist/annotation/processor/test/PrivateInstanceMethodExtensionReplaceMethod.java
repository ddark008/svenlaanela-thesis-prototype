package org.zeroturnaround.javassist.annotation.processor.test;

import org.zeroturnaround.javassist.annotation.Patches;

@Patches(PrivateInstanceMethod.class)
public class PrivateInstanceMethodExtensionReplaceMethod extends PrivateInstanceMethod_Mirror {
  @Override
  public String method(String $1) {
    return "Hello world!";
  }
}
