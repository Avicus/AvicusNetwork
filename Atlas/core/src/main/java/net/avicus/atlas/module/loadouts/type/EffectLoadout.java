package net.avicus.atlas.module.loadouts.type;

import java.util.List;
import javax.annotation.Nullable;
import lombok.ToString;
import net.avicus.atlas.module.loadouts.Loadout;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

@ToString(callSuper = true)
public class EffectLoadout extends Loadout {

  private final List<PotionEffect> effects;

  public EffectLoadout(boolean force, @Nullable Loadout parent, List<PotionEffect> effects) {
    super(force, parent);
    this.effects = effects;
  }

  @Override
  public void give(Player player, boolean force) {
    // Effects
    for (PotionEffect effect : this.effects) {
      player.addPotionEffect(effect, this.isForce() || force);
    }
  }
}
