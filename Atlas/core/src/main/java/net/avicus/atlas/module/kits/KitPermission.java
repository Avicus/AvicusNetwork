package net.avicus.atlas.module.kits;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class KitPermission {

  private final String node;
  private final boolean value;

  public KitPermission(String node, boolean value) {
    this.node = node;
    this.value = value;
  }
}
