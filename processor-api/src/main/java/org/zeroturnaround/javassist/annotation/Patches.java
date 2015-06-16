package org.zeroturnaround.javassist.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Defines the class to patch
 */
@Target(value={ElementType.TYPE})
public @interface Patches {
  public Class<?> value();
}