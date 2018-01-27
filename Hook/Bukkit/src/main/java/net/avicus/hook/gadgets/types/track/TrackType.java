package net.avicus.hook.gadgets.types.track;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum TrackType {
  THIRTEEN("13", Material.GOLD_RECORD),
  CAT("Cat", Material.GREEN_RECORD),
  BLOCKS("Blocks", Material.RECORD_3),
  CHIRP("Chirp", Material.RECORD_4),
  FAR("Far", Material.RECORD_5),
  MALL("Mall", Material.RECORD_6),
  MELLOHI("Mellohi", Material.RECORD_7),
  STAL("Stal", Material.RECORD_8),
  STRAD("Strad", Material.RECORD_9),
  WARD("Ward", Material.RECORD_10),
  ELEVEN("11", Material.RECORD_11),
  WAIT("Wait", Material.RECORD_12);

  private final String name;
  private final Material material;

  TrackType(String name, Material material) {
    this.name = name;
    this.material = material;
  }
}
