package net.avicus.atlas.module.invsee;

import java.util.Iterator;
import java.util.Map;
import net.avicus.atlas.util.AtlasTask;
import org.bukkit.entity.Player;

public class InvSeeTask extends AtlasTask {

  private final Map<Player, TrackedInventory> opened;

  public InvSeeTask(Map<Player, TrackedInventory> opened) {
    super();
    this.opened = opened;
  }

  @Override
  public void run() {
    Iterator<Player> iterator = this.opened.keySet().iterator();

    while (iterator.hasNext()) {
      Player player = iterator.next();
      TrackedInventory tracked = this.opened.get(player);

      if (!tracked.isOpen()) {
        // Remove tracked inventories if they aren't open anymore
        iterator.remove();
      } else {
        // Otherwise, update them
        tracked.update();
      }
    }
  }
}
