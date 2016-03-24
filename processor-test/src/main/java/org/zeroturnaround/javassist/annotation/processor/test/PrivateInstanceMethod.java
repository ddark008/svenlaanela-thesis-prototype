package org.zeroturnaround.javassist.annotation.processor.test;

public class PrivateInstanceMethod {
  public String accessPrivate(String input) {
    return method(input);
  }

  private String method(String input) {
    return input.substring(input.length()/2) + input.substring(0, input.length()/2);
  }
}
