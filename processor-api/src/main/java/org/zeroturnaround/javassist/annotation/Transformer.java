package org.zeroturnaround.javassist.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * An annotation marking a class bytecode transformer class for a given class
 * specified by the String value. After the annotation processor has constructed
 * a Transformer class for a given original class and its extension class, the
 * Transformer class is marked with the @Transformer annotation.
 * 
 * Used by ClassPathTransformerLoader to find instances of the transformer class
 */
@Target(value={ElementType.TYPE})
public @interface Transformer {
  String value();
}
