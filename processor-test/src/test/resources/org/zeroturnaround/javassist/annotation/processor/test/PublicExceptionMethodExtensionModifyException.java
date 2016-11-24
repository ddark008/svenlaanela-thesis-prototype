package org.zeroturnaround.javassist.annotation.processor.test;

import org.zeroturnaround.javassist.annotation.Patches;

@Patches(PublicExceptionMethod.class)
public class PublicExceptionMethodExtensionModifyException extends PublicExceptionMethod_Mirror {
  @Override
  public String method() throws Exception {
    try {
      return super.method();
    } catch (Exception e) {
      if ("original exception".equals(e.getMessage())) {
        throw new Exception("modified exception");
      } else {
        throw e;
      }
    }
  }
}
