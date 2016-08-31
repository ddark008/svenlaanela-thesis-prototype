package org.zeroturnaround.javassist.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation marking a field, method or constructor of an extension class as
 * replacing some field, method or constructor of the original class.
 * </p>
 *
 * <p>
 * The original class must have a field, method or constructor that matches the following
 * </p>
 *
 * Field:
 * <ul>
 * <li>type</li>
 * <li>name</li>
 * </ul>
 *
 * Method:
 * <ul>
 * <li>name</li>
 * <li>list of argument types</li>
 * </ul>
 *
 * Constructor:
 * <ul>
 * <li>list of argument types</li>
 * </ul>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(value={ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Modify {}