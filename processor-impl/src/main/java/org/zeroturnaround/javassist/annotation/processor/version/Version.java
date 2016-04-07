package org.zeroturnaround.javassist.annotation.processor.version;

public class Version implements Comparable<Version> {
  public static final Version ANY = new AnyVersion();
  public final int major;
  public final int minor;
  public final int micro;

  private Version() {
    major = -1;
    minor = -1;
    micro = -1;
  }

  public Version(String version) {
    if (version == null) {
      throw new IllegalArgumentException("version cannot be null!");
    }
    String[] tokens = version.split(".");
    if (tokens.length != 3) {
      throw new IllegalArgumentException("version does not conform to [major].[minor].[micro]");
    }
    try {
      major = Integer.parseInt(tokens[0]);
      minor = Integer.parseInt(tokens[1]);
      micro = Integer.parseInt(tokens[2]);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("version does not conform to [major].[minor].[micro]", e);
    }
  }

  public int compareTo(Version other) {
    if (other == null) {
      return 1;
    } else if (other instanceof AnyVersion) {
      return 0;
    } else {
      if (this.major == other.major) {
        if (this.minor == other.minor) {
          if (this.micro == other.micro) {
            return 0;
          } else {
            return this.micro - other.micro;
          }
        } else {
          return this.minor - other.minor;
        }
      } else {
        return this.major - other.major;
      }
    }
  }

  public static Version of(String version) {
    if (version == null || "".equals(version) || "any".equals(version)) {
      return Version.ANY;
    } else {
      return new Version(version);
    }
  }

  /**
   * AnyVersion compares
   */
  private static class AnyVersion extends Version {
    public int compareTo(Version other) {
      if (other == null) {
        return 1;
      } else {
        return 0;
      }
    }

    public String toString() {
      return "any";
    }
  }

  public String toString() {
    return String.format("%d.%d.%d", major, minor, micro);
  }
}
