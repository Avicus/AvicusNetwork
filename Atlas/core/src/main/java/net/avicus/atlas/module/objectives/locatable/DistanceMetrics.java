package net.avicus.atlas.module.objectives.locatable;

import javax.annotation.Nullable;
import lombok.Getter;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.magma.util.distance.DistanceCalculationMetric;

public class DistanceMetrics {

  @Getter
  private
  @Nullable
  final DistanceCalculationMetric preCompleteMetric;
  @Getter
  private
  @Nullable
  final DistanceCalculationMetric postCompleteMetric;

  public DistanceMetrics(DistanceCalculationMetric preCompleteMetric,
      DistanceCalculationMetric postCompleteMetric) {
    this.preCompleteMetric = preCompleteMetric;
    this.postCompleteMetric = postCompleteMetric;
  }

  public static DistanceCalculationMetric parse(XmlElement el, DistanceCalculationMetric def)
      throws XmlException {
    return parse(el, "", def);
  }

  public static DistanceCalculationMetric parse(XmlElement el, String prefix) throws XmlException {
    return parse(el, prefix, null);
  }

  public static DistanceCalculationMetric parse(XmlElement el, String prefix,
      DistanceCalculationMetric def) throws XmlException {
    if (!prefix.isEmpty()) {
      prefix = prefix + "-";
    }

    if (def != null) {
      return new DistanceCalculationMetric(
          el.getAttribute(prefix + "dist-metric").asEnum(DistanceCalculationMetric.Type.class, true)
              .orElse(def.type),
          el.getAttribute(prefix + "dist-horiz").asBoolean().orElse(def.horizontal));
    } else {
      if (!el.hasAttribute(prefix + "dist-metric")) {
        return null;
      }

      return new DistanceCalculationMetric(el.getAttribute(prefix + "dist-metric")
          .asRequiredEnum(DistanceCalculationMetric.Type.class, true),
          el.getAttribute(prefix + "dist-horiz").asBoolean().orElse(false));
    }
  }

  public static class Builder {

    public
    @Nullable
    DistanceCalculationMetric preComplete;
    public
    @Nullable
    DistanceCalculationMetric postCompleteMetric;

    public Builder preComplete(XmlElement el, @Nullable DistanceCalculationMetric def) {
      return preComplete(el, "", def);
    }

    public Builder preComplete(XmlElement el, String prefix,
        @Nullable DistanceCalculationMetric def) {
      this.preComplete = parse(el, prefix, def);
      return this;
    }

    public Builder postComplete(XmlElement el, @Nullable DistanceCalculationMetric def) {
      return postComplete(el, "", def);
    }

    public Builder postComplete(XmlElement el, String prefix,
        @Nullable DistanceCalculationMetric def) {
      this.postCompleteMetric = parse(el, prefix, def);
      return this;
    }

    public DistanceMetrics build() {
      return new DistanceMetrics(preComplete, postCompleteMetric);
    }
  }
}
