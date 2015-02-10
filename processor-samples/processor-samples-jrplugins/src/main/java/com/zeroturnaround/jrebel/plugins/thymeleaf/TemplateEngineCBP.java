package com.zeroturnaround.jrebel.plugins.thymeleaf;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateEngine_Mirror;
import org.thymeleaf.cache.ICacheManager;
import org.zeroturnaround.javassist.annotation.Patches;

@Patches(TemplateEngine.class)
public class TemplateEngineCBP extends TemplateEngine_Mirror {

	@Override
	public void setCacheManager(ICacheManager $1) {
	}
	
}
