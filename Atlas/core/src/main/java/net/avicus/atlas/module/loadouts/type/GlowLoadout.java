package net.avicus.atlas.module.loadouts.type;

import javax.annotation.Nullable;
import lombok.ToString;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.atlas.util.VersionUtil;
import net.avicus.compendium.snap.SnapClass;
import net.avicus.compendium.snap.SnapMethod;
import org.bukkit.entity.Player;

@ToString(callSuper = true)
public class GlowLoadout extends Loadout {

  private final boolean glow;

  public GlowLoadout(boolean force, @Nullable Loadout parent, boolean glow) {
    super(force, parent);
    this.glow = glow;
  }

  @Override
  public void give(Player player, boolean force) {
    // 1.8-1.9 Start
    if (VersionUtil.isCombatUpdate()) {
      // Glow effect
      if (this.glow) {
        SnapMethod setGlowing = new SnapClass(player.getClass())
            .getMethod("setGlowing", boolean.class);
        setGlowing.get(player, true);
        // 1.9 Version
        // player.setGlowing(this.glow.getFirst());
      }
    }
  }
}
