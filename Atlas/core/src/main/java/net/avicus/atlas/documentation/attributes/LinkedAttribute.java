package net.avicus.atlas.documentation.attributes;

import lombok.Getter;

public class LinkedAttribute implements Attribute {

  private final Object object;
  @Getter
  private final String link;
  @Getter
  private final String[] description;
  @Getter
  private boolean required;

  public LinkedAttribute(Object object, String link, boolean required, String... description) {
    this.object = object;
    this.link = link;
    this.required = required;
    this.description = description;
  }

  @Override
  public String getName() {
    return this.object.getClass().getSimpleName();
  }
}
