package net.avicus.atlas.module.loadouts.type;

import java.util.List;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.loadouts.Loadout;
import org.bukkit.entity.Player;

@ToString(callSuper = true)
public class LoadoutNode extends Loadout {

  @Getter
  private final List<Loadout> loadouts;

  public LoadoutNode(boolean force, @Nullable Loadout parent, List<Loadout> loadouts) {
    super(force, parent);
    this.loadouts = loadouts;
  }

  @Override
  public void give(Player player, boolean force) {
    this.loadouts.forEach(l -> l.give(player, force));
  }
}
