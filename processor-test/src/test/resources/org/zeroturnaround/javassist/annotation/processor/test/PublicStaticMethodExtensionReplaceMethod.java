package org.zeroturnaround.javassist.annotation.processor.test;

import org.zeroturnaround.javassist.annotation.Modify;
import org.zeroturnaround.javassist.annotation.Patches;

@Patches(PublicStaticMethod.class)
public class PublicStaticMethodExtensionReplaceMethod extends PublicStaticMethod_Mirror {
  @Modify
  public static String method(String $1) {
    return "replaced " + $1;
  }
}
