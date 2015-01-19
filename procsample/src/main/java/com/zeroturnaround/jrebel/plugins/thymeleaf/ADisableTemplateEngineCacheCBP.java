package com.zeroturnaround.jrebel.plugins.thymeleaf;

import org.thymeleaf.cache.ICacheManager;
import org.zeroturnaround.javassist.annotation.Method;
import org.zeroturnaround.javassist.annotation.Patches;

/**
 * Annotate the class that we are patching
 */
@Patches(org.thymeleaf.TemplateEngine.class)
public class ADisableTemplateEngineCacheCBP {

  @Method
  public void setCacheManager(ICacheManager manager) {
  }

}
