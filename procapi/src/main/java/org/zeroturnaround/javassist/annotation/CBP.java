package org.zeroturnaround.javassist.annotation;

/**
 * Created by lanza on 25/08/14.
 */
public interface CBP {

  public void proceed(Object... args);

  public <T> T proceed(Class<T> type, Object... args);

}
