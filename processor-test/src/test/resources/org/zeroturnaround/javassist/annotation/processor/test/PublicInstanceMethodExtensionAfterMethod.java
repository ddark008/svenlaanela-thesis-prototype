package org.zeroturnaround.javassist.annotation.processor.test;

import org.zeroturnaround.javassist.annotation.Patches;

@Patches(PublicInstanceMethod.class)
public class PublicInstanceMethodExtensionAfterMethod extends PublicInstanceMethod_Mirror {
  @Override
  public String method(String $1) {
    return super.method($1) + "abc";
  }
}
