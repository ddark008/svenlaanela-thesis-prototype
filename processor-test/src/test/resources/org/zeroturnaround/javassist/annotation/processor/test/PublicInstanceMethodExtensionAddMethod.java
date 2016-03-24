package org.zeroturnaround.javassist.annotation.processor.test;

import org.zeroturnaround.javassist.annotation.Patches;

@Patches(PublicInstanceMethod.class)
public class PublicInstanceMethodExtensionAddMethod extends PublicInstanceMethod_Mirror {

  public String addedMethod() {
    return "addedMethod!";
  }
}
