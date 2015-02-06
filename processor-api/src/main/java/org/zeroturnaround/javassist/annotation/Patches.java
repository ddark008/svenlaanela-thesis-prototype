package org.zeroturnaround.javassist.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the type to patch and the versions this patch supports.
 *
 * Created by lanza on 21/06/14.
 */
@Target(value={ElementType.TYPE})
public @interface Patches {
  public Class<?> value();

//  public Version version() default Version.ALL;
}
