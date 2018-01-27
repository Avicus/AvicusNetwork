package net.avicus.atlas.module.loadouts.type;

import javax.annotation.Nullable;
import lombok.ToString;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.FallingBlockWatcher;
import net.avicus.atlas.module.loadouts.Loadout;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@ToString(callSuper = true)
public class DisguiseLoadout extends Loadout {

  private final DisguiseType disguise;
  private final
  @Nullable
  Material material;
  private final boolean baby;

  public DisguiseLoadout(boolean force, @Nullable Loadout parent, DisguiseType disguise,
      Material material, boolean baby) {
    super(force, parent);
    this.disguise = disguise;
    this.material = material;
    this.baby = baby;
  }

  @Override
  public void give(Player player, boolean force) {
    Disguise disguise;
    if (this.disguise.isMob()) {
      disguise = new MobDisguise(this.disguise, this.baby);
    } else if (this.disguise.isMisc()) {
      disguise = new MiscDisguise(this.disguise);
    } else {
      throw new UnsupportedOperationException(
          "Attempted to disguise " + player.getName() + " as a player.");
    }
    if (this.disguise.isMisc() && this.material != null) {
      ((FallingBlockWatcher) disguise.getWatcher()).setBlock(new ItemStack(this.material));
    }
    DisguiseAPI.disguiseToAll(player, disguise);
  }
}
