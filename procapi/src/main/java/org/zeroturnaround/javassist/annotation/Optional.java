package org.zeroturnaround.javassist.annotation;

/**
 * Created by lanza on 25/08/14.
 */
public class Optional<T> {
  public static final Optional NONE = new Optional(null);
  public final T value;

  private Optional(T value) {
    this.value = value;
  }

  public static <T> Optional<T> of(T value) {
    return new Optional(value);
  }

}
