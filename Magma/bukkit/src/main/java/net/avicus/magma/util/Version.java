package net.avicus.magma.util;

import lombok.Getter;

public class Version implements Comparable<Version> {

  @Getter
  private final int major;
  @Getter
  private final int minor;
  @Getter
  private final int patch;

  public Version(int major, int minor, int patch) {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
  }

  @Override
  public String toString() {
    return String.valueOf(this.major + "." + this.minor + "." + this.patch);
  }

  @Override
  public int compareTo(Version o) {
    if (this.getMajor() > o.getMajor()) {
      return 1;
    }
    if (this.getMajor() < o.getMajor()) {
      return -1;
    }
    if (this.getMinor() > o.getMinor()) {
      return 1;
    }
    if (this.getMinor() < o.getMinor()) {
      return -1;
    }
    if (this.getPatch() > o.getPatch()) {
      return 1;
    }
    if (this.getPatch() < o.getPatch()) {
      return -1;
    }

    return 0;
  }

  public boolean greaterEqual(Version that) {
    return this.major >= that.major
        && this.minor >= that.minor
        && this.patch >= that.patch;
  }
}
