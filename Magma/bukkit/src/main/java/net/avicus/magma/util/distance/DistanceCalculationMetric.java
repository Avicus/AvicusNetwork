package net.avicus.magma.util.distance;

public class DistanceCalculationMetric {

  public final Type type;
  public final boolean horizontal;

  public DistanceCalculationMetric(Type type, boolean horizontal) {
    this.type = type;
    this.horizontal = horizontal;
  }

  public String name() {
    if (this.horizontal) {
      return this.type.name() + "_HORIZONTAL";
    } else {
      return this.type.name();
    }
  }

  public String description() {
    if (this.horizontal) {
      return this.type.description + " (horizontal)";
    } else {
      return this.type.description;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DistanceCalculationMetric)) {
      return false;
    }
    DistanceCalculationMetric that = (DistanceCalculationMetric) o;
    return this.type == that.type &&
        this.horizontal == that.horizontal;
  }

  @Override
  public int hashCode() {
    int result = type.hashCode();
    result = 31 * result + (horizontal ? 1 : 0);
    return result;
  }

  public static enum Type {
    PLAYER("closest player"),
    BLOCK("closest block"),
    KILL("closest kill"),
    STATIC("relation to point");

    public final String description;

    Type(String description) {
      this.description = description;
    }
  }
}
