package org.zeroturnaround.javassist.annotation.processor.test;

import org.zeroturnaround.javassist.annotation.Modify;
import org.zeroturnaround.javassist.annotation.Patches;

@Patches(PublicInstanceMethod.class)
public class PublicInstanceMethodExtensionAddMethodCallFromExisting extends PublicInstanceMethod_Mirror {
  public String addedMethod() {
    return "addedMethodCalledFromExisting!";
  }

  @Modify
  public String access(String input) {
    return addedMethod();
  }
}
