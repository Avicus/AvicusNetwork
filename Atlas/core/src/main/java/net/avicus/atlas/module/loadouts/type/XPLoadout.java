package net.avicus.atlas.module.loadouts.type;

import javax.annotation.Nullable;
import lombok.ToString;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.compendium.number.PreparedNumberAction;
import org.bukkit.entity.Player;

@ToString(callSuper = true)
public class XPLoadout extends Loadout {

  private final PreparedNumberAction level;
  private final PreparedNumberAction exp;
  private final PreparedNumberAction totalExp;

  public XPLoadout(boolean force, @Nullable Loadout parent, PreparedNumberAction level,
      PreparedNumberAction exp, PreparedNumberAction totalExp) {
    super(force, parent);
    this.level = level;
    this.exp = exp;
    this.totalExp = totalExp;
  }

  @Override
  public void give(Player player, boolean force) {
    // Experience
    if (this.level != null) {
      player.setLevel(this.level.perform(player.getLevel()));
    }
    if (this.exp != null) {
      player.setExp(this.exp.perform(player.getExp()));
    }
    if (this.totalExp != null) {
      player.setTotalExperience(this.totalExp.perform(player.getTotalExperience()));
    }
  }
}
