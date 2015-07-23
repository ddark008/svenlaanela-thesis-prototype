package org.zeroturnaround.javassist.annotation.processor.test;

public class PublicInstanceMethod {
  
  public String access(String input) {
    return method(input);
  }
  
  public String method(String input) {
    return Util.reverse(input);
  }

}

class Util {
  public static final String reverse(String input) {
    String s = "";
    for (int i = input.length() -1; i >= 0; i--) {
      s += input.charAt(i);
    }
    return s;
  }
}