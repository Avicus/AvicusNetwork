package net.avicus.magma.util;

import javax.annotation.Nullable;

public enum MapGenre {
  NEBULA,
  KOTH,
  CTF,
  TDM,
  MIX, // This is not determined by code, map authors need to specify.
  ELIMINATION,
  SKY_WARS,
  WALLS,
  ARCADE;

  MapGenre() {
  }

  @Nullable
  public static MapGenre of(final String string) {
    for (final MapGenre type : values()) {
      if (type.name().equalsIgnoreCase(string)) {
        return type;
      }
    }
    return null;
  }
}
