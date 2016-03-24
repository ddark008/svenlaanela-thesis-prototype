package org.zeroturnaround.javassist.annotation.processor.test;

import org.zeroturnaround.javassist.annotation.Modify;
import org.zeroturnaround.javassist.annotation.Patches;

import java.lang.Override;

/**
 * I do not really like the concept of Mirrors :(
 */
@Patches(PublicInstanceMethod.class)
public class PublicInstanceMethodExtensionNonExistentMethod extends PublicInstanceMethod_Mirror {
  @Modify
  public String nonExistentMethod(String $1) {
    return "Hello world!";
  }
}