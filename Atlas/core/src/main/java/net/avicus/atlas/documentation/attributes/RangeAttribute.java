package net.avicus.atlas.documentation.attributes;

import lombok.Getter;

public class RangeAttribute implements Attribute {

  private final Class<? extends Number> numberClazz;
  @Getter
  private final String[] values;
  @Getter
  private final boolean required;
  @Getter
  private final String[] description;

  public RangeAttribute(Number start, Number end, boolean required, String... description) {
    this.numberClazz = start.getClass();
    this.values = new String[]{start + "-" + end};
    this.required = required;
    this.description = description;
  }

  @Override
  public String getName() {
    return this.numberClazz.getSimpleName() + " Range";
  }
}
