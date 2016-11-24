package org.zeroturnaround.javassist.annotation.processor.test;

public class PublicExceptionMethod {

  public String access() {
    try {
      method();
      return null;
    } catch (Exception e) {
      return e.getMessage();
    }
  }

  public String method() throws Exception {
    throw new Exception("original exception");
  }
}
