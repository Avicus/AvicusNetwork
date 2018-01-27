package net.avicus.atlas.module.loadouts;

import javax.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;

@Getter
@ToString
public abstract class Loadout {

  private final boolean force;
  private final
  @Nullable
  Loadout parent;

  public Loadout(boolean force, @Nullable Loadout parent) {
    this.force = force;
    this.parent = parent;
  }

  public void apply(Player player, boolean force) {
    if (this.parent != null) {
      this.parent.apply(player, force);
    }
    give(player, force);
  }

  public void apply(Player player) {
    apply(player, false);
  }

  public abstract void give(Player player, boolean force);
}
