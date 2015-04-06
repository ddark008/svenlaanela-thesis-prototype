package org.zeroturnaround.javassist.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lanza on 25/08/14.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(value={ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Modify {

}
