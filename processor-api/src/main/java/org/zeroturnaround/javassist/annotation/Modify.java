package org.zeroturnaround.javassist.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation marking a field, method or constructor of an extension class as
 * replacing some field, method or constructor of the original class.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(value={ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Modify {}