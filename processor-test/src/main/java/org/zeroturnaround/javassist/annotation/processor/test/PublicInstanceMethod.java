package org.zeroturnaround.javassist.annotation.processor.test;

public class PublicInstanceMethod {
  
  public String access(String input) {
    return method(input);
  }
  
  public String method(String input) {
    return Util.reverse(input);
  }

}