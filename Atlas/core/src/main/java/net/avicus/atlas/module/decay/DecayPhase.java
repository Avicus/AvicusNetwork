package net.avicus.atlas.module.decay;

import lombok.Getter;
import lombok.ToString;
import org.bukkit.Material;
import org.joda.time.Duration;

@ToString
public class DecayPhase {

  @Getter
  private final Duration delay;
  @Getter
  private final Material material;
  @Getter
  private final byte data;

  public DecayPhase(Duration delay, Material material, byte data) {
    this.delay = delay;
    this.material = material;
    this.data = data;
  }
}
