package net.avicus.atlas.sets.competitve.objectives.flag;

import javax.annotation.Nullable;
import lombok.Getter;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.magma.util.distance.DistanceCalculationMetric;

public class FlagDistanceMetrics extends
    net.avicus.atlas.module.objectives.locatable.DistanceMetrics {

  @Getter
  private
  @Nullable
  final DistanceCalculationMetric carryingMetric;

  public FlagDistanceMetrics(DistanceCalculationMetric preComplete,
      DistanceCalculationMetric postCompleteMetric, DistanceCalculationMetric carryingMetric) {
    super(preComplete, postCompleteMetric);
    this.carryingMetric = carryingMetric;
  }

  public static class Builder extends
      net.avicus.atlas.module.objectives.locatable.DistanceMetrics.Builder {

    private
    @Nullable
    DistanceCalculationMetric carryMetric;

    public Builder carry(XmlElement el, @Nullable DistanceCalculationMetric def) {
      return carry(el, "", def);
    }

    public Builder carry(XmlElement el, String prefix, @Nullable DistanceCalculationMetric def) {
      this.carryMetric = parse(el, prefix, def);
      return this;
    }

    @Override
    public Builder preComplete(XmlElement el, @Nullable DistanceCalculationMetric def) {
      return (Builder) super.preComplete(el, def);
    }

    @Override
    public Builder preComplete(XmlElement el, String prefix,
        @Nullable DistanceCalculationMetric def) {
      return (Builder) super.preComplete(el, prefix, def);
    }

    @Override
    public Builder postComplete(XmlElement el, @Nullable DistanceCalculationMetric def) {
      return (Builder) super.postComplete(el, def);
    }

    @Override
    public Builder postComplete(XmlElement el, String prefix,
        @Nullable DistanceCalculationMetric def) {
      return (Builder) super.postComplete(el, prefix, def);
    }

    public FlagDistanceMetrics build() {
      return new FlagDistanceMetrics(preComplete, postCompleteMetric, carryMetric);
    }
  }
}
