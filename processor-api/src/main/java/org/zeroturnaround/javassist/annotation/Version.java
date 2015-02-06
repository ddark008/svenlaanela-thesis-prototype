package org.zeroturnaround.javassist.annotation;

/**
 * Created by lanza on 25/08/14.
 */
public enum Version {

  ALL;

  private final String from;

  private final String to;

  private Version() {
    this.from = "";
    this.to = "";
  }

  // TODO: missing implementation.. I dont thin we can use enums for this... probably have to go with just strings.

}
