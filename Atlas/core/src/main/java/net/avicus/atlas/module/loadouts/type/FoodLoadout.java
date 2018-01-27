package net.avicus.atlas.module.loadouts.type;

import javax.annotation.Nullable;
import lombok.ToString;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.compendium.number.PreparedNumberAction;
import org.bukkit.entity.Player;

@ToString(callSuper = true)
public class FoodLoadout extends Loadout {

  private final PreparedNumberAction foodLevel;
  private final PreparedNumberAction saturation;

  public FoodLoadout(boolean force, @Nullable Loadout parent, PreparedNumberAction foodLevel,
      PreparedNumberAction saturation) {
    super(force, parent);
    this.foodLevel = foodLevel;
    this.saturation = saturation;
  }

  @Override
  public void give(Player player, boolean force) {
    // Food
    if (this.foodLevel != null) {
      player.setFoodLevel(this.foodLevel.perform(player.getFoodLevel()));
    }
    if (this.saturation != null) {
      player.setSaturation(this.saturation.perform(player.getSaturation()));
    }
  }
}
