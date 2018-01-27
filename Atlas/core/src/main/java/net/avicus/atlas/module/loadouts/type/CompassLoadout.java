package net.avicus.atlas.module.loadouts.type;

import javax.annotation.Nullable;
import lombok.ToString;
import net.avicus.atlas.module.loadouts.Loadout;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@ToString(callSuper = true)
public class CompassLoadout extends Loadout {

  private final Vector target;

  public CompassLoadout(boolean force, @Nullable Loadout parent, Vector target) {
    super(force, parent);
    this.target = target;
  }

  @Override
  public void give(Player player, boolean force) {
    player.setCompassTarget(this.target.toLocation(player.getWorld()));
  }
}
