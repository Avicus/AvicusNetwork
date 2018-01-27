package net.avicus.atlas.module.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.util.Events;
import org.bukkit.event.Listener;

@ToString(exclude = "match")
public class ItemsModule implements Module {

  @Getter
  private final Match match;
  private final List<Listener> listeners;

  public ItemsModule(Match match, Optional<Check> removeDrops, Optional<Check> keepItems,
      Optional<Check> keepArmor, Optional<Check> repairTools) {
    this.match = match;

    this.listeners = new ArrayList<>();
    if (removeDrops.isPresent()) {
      this.listeners.add(new RemoveDropsListener(match, removeDrops.get()));
    }

    if (keepItems.isPresent() || keepArmor.isPresent()) {
      this.listeners.add(new KeepListener(match, keepItems, keepArmor));
    }

    if (repairTools.isPresent()) {
      this.listeners.add(new RepairToolsListener(match, repairTools.get()));
    }
  }

  @Override
  public void open() {
    Events.register(this.listeners);
  }

  @Override
  public void close() {
    Events.unregister(this.listeners);
  }
}
