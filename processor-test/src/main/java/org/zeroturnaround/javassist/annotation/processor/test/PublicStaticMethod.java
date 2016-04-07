package org.zeroturnaround.javassist.annotation.processor.test;

public class PublicStaticMethod {
  public static String access(String input) {
    return method(input);
  }

  public static String method(String input) {
    return Util.reverse(input);
  }
}
