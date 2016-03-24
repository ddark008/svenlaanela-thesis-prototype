package org.zeroturnaround.javassist.annotation.processor.test;

public class PrivateInstanceMethod {
  public String accessPrivate(String input) {
    return method(input);
  }

  private String method(String input) {
    return Util.reverse(input);
  }
}
