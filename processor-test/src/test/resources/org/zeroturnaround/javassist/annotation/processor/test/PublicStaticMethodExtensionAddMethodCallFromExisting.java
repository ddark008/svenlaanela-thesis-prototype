package org.zeroturnaround.javassist.annotation.processor.test;

import org.zeroturnaround.javassist.annotation.Modify;
import org.zeroturnaround.javassist.annotation.Patches;

@Patches(PublicStaticMethod.class)
public class PublicStaticMethodExtensionAddMethodCallFromExisting extends PublicStaticMethod_Mirror {

  public static String reverse(String $1) {
    return Util.reverse($1);
  }

  @Modify
  public static String method(String $1) {
    return reverse($1);
  }
}
