package org.zeroturnaround.javassist.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * <p>
 * Defines the class to patch. The existence of this annotation on a class during compilation signals to
 * the Java Annotation Processor engine, that this is a byte-code modifying class for the class given as
 * an argument to the annotation.
 * </p>
 *
 * <p>
 * The Java annotation processor then generates a liberal mirror class for the class under modification that the
 * byte-code modifying class can extend from to provide type-safe access to existing members of the modified class
 * </p>
 *
 * <p>
 * For example, given a class SampleClass with a single method greet like below:
 * <pre>
 * class SampleClass {
 *     public String greet(String name) {
 *         return "Hello, " + name;
 *     }
 * }
 * </pre>
 * </p>
 *
 * <p>
 * If a developer would like to modify the byte-code of this class and, for example, add a null check for the argument
 * name, it could be done by writing an extension class like so:
 *
 * <pre>
 * &#064;Patches(SampleClass.class)
 * public class SampleClassExtension extends SampleClass_Mirror {
 *     &#064;Override
 *     public String greet(String name) {
 *         if (name == null) {
 *             return name;
 *         }
 *         return super.greet(name);
 *     }
 * }
 * </pre>
 * </p>
 *
 */
@Target(value={ElementType.TYPE})
public @interface Patches {
  /**
   * The class whose byte-code is modified by a class annotated with this annotation.
   */
  Class<?> value();

  String min() default "";

  String max() default "";
}