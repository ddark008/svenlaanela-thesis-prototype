package org.zeroturnaround.javassist.annotation.processor.version;

public class VersionRange {
  public final Version min;
  public final Version max;

  public VersionRange(String min, String max) {
    this(Version.of(min), Version.of(max));
  }

  public VersionRange(Version min, Version max) {
    this.min = min;
    this.max = max;
  }

  public String toString() {

    return String.format("VersionRange: [%s-%s]", min, max);
  }
}
