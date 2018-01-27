package net.avicus.atlas.module.zones;

import lombok.Getter;
import net.avicus.compendium.utils.Strings;

public enum ZoneMessageFormat {
  INFO(" ^8[^a✳^8] ^7%s"),
  WARNING(" ^8[^e^l!^8] ^c%s"),
  ALERT(" ^8[^c^l!^8] ^c%s"),
  NONE("%s");

  @Getter
  private final String format;

  ZoneMessageFormat(String format) {
    this.format = Strings.addColors(format);
  }

  public String format(String text) {
    return String.format(this.format, text);
  }
}
