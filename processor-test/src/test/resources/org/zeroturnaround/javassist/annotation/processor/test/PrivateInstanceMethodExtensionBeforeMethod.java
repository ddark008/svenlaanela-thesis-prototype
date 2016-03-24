package org.zeroturnaround.javassist.annotation.processor.test;

import org.zeroturnaround.javassist.annotation.Patches;

@Patches(PrivateInstanceMethod.class)
public class PrivateInstanceMethodExtensionBeforeMethod extends PrivateInstanceMethod_Mirror {
  @Override
  public String method(String $1) {
    return "xyz" + super.method($1);
  }
}
