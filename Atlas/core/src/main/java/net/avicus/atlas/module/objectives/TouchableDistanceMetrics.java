package net.avicus.atlas.module.objectives;

import javax.annotation.Nullable;
import lombok.Getter;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.magma.util.distance.DistanceCalculationMetric;

public class TouchableDistanceMetrics extends
    net.avicus.atlas.module.objectives.locatable.DistanceMetrics {

  @Getter
  private
  @Nullable
  final DistanceCalculationMetric postTouchMetric;

  public TouchableDistanceMetrics(DistanceCalculationMetric preComplete,
      DistanceCalculationMetric postCompleteMetric, DistanceCalculationMetric postTouchMetric) {
    super(preComplete, postCompleteMetric);
    this.postTouchMetric = postTouchMetric;
  }

  public static class Builder extends
      net.avicus.atlas.module.objectives.locatable.DistanceMetrics.Builder {

    private
    @Nullable
    DistanceCalculationMetric postTouchMetric;

    public Builder postTouch(XmlElement el, @Nullable DistanceCalculationMetric def) {
      return postTouch(el, "", def);
    }

    public Builder postTouch(XmlElement el, String prefix,
        @Nullable DistanceCalculationMetric def) {
      this.postTouchMetric = parse(el, prefix, def);
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

    public TouchableDistanceMetrics build() {
      return new TouchableDistanceMetrics(preComplete, postCompleteMetric, postTouchMetric);
    }
  }
}
