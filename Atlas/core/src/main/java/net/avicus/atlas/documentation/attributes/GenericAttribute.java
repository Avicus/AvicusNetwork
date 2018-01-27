package net.avicus.atlas.documentation.attributes;

import java.util.UUID;
import lombok.Getter;
import net.avicus.atlas.module.locales.LocalizedXmlString;

public class GenericAttribute implements Attribute {

  private final Class clazz;
  @Getter
  private final String[] description;
  @Getter
  private boolean required;

  public GenericAttribute(Class clazz, boolean required, String... description) {
    this.clazz = clazz;
    this.required = required;
    this.description = description;
  }

  @Override
  public String getName() {
    if (this.clazz == Boolean.class) {
      return "True/False";
    }
    if (this.clazz == LocalizedXmlString.class) {
      return "Localized String";
    }
    if (this.clazz == String.class) {
      return "Text";
    }
    if (this.clazz == Integer.class) {
      return "Number (Without Decimal)";
    }
    if (this.clazz == Double.class) {
      return "Number (With Decimal)";
    }
    if (this.clazz == UUID.class) {
      return "Minecraft UUID";
    }

    return this.clazz.getSimpleName();
  }
}
