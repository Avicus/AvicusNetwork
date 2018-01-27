package net.avicus.atlas.module.loadouts.type;

import javax.annotation.Nullable;
import lombok.ToString;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.compendium.number.PreparedNumberAction;
import org.bukkit.entity.Player;

@ToString(callSuper = true)
public class MovementLoadout extends Loadout {

  private final PreparedNumberAction exhaustion;
  private final PreparedNumberAction flySpeed;
  private final PreparedNumberAction walkSpeed;

  public MovementLoadout(boolean force, @Nullable Loadout parent, PreparedNumberAction exhaustion,
      PreparedNumberAction flySpeed, PreparedNumberAction walkSpeed) {
    super(force, parent);
    this.exhaustion = exhaustion;
    this.flySpeed = flySpeed;
    this.walkSpeed = walkSpeed;
  }

  @Override
  public void give(Player player, boolean force) {
    // Movement
    if (this.exhaustion != null) {
      player.setExhaustion(this.exhaustion.perform(player.getExhaustion()));
    }
    if (this.flySpeed != null) {
      player.setFlySpeed(this.flySpeed.perform(player.getFlySpeed()));
    }
    if (this.walkSpeed != null) {
      player.setWalkSpeed(this.walkSpeed.perform(player.getWalkSpeed()));
    }
  }
}
