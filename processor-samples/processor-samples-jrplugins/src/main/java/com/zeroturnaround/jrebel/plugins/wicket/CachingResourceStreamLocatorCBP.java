package com.zeroturnaround.jrebel.plugins.wicket;

import org.apache.wicket.core.util.resource.locator.caching.CachingResourceStreamLocator;
import org.zeroturnaround.javassist.annotation.Patches;

@Patches(CachingResourceStreamLocator.class)
public class CachingResourceStreamLocatorCBP {

}
