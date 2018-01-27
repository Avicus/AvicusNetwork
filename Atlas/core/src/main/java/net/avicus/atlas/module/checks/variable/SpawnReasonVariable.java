package net.avicus.atlas.module.checks.variable;

import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.checks.Variable;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

/**
 * The spawn reason variable contains information about the reason an entity was spawned.
 */
@ToString
public class SpawnReasonVariable implements Variable {

  @Getter
  private final SpawnReason reason;

  public SpawnReasonVariable(SpawnReason reason) {
    this.reason = reason;
  }
}
