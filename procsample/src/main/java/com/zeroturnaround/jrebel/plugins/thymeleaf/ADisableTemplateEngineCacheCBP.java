package com.zeroturnaround.jrebel.plugins.thymeleaf;

import org.thymeleaf.cache.ICacheManager;
import org.zeroturnaround.javassist.annotation.Before;
import org.zeroturnaround.javassist.annotation.Patches;

/**
 * Annotate the class that we are patching
 */
@Patches(org.thymeleaf.TemplateEngine.class)
public class ADisableTemplateEngineCacheCBP {

  @Before
  public void setCacheManager(ICacheManager manager) {
  }

}
