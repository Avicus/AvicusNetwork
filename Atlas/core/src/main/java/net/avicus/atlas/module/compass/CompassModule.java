package net.avicus.atlas.module.compass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CompassModule implements Module {

  private final Match match;
  private final CompassUpdateTask updateTask;

  public CompassModule(Match match, List<Compass> compasses) {
    this.match = match;
    this.updateTask = new CompassUpdateTask(this.match, this, compasses);
  }

  @Override
  public void open() {
    this.updateTask.repeat(0, 5);
  }

  @Override
  public void close() {
    this.updateTask.cancel0();
  }

  public void setCompasses(Player player, Optional<CompassView> compass) {
    Map<Integer, ItemStack> map = getCompasses(player);

    if (compass.isPresent()) {
      player.setCompassTarget(compass.get().getTarget());
    } else {
      player.setCompassTarget(player.getLocation());
    }

    for (int index : map.keySet()) {
      ItemStack before = map.get(index);
      ItemStack after = new ItemStack(before.getType(), before.getAmount(),
          before.getData().getData());

      if (compass.isPresent()) {
        String name = compass.get().getDisplay().render(player).toLegacyText();
        ItemMeta meta = after.getItemMeta();
        meta.setDisplayName(name);
        after.setItemMeta(meta);
      }

      replaceCompass(player, index, after);
    }
  }

  public Map<Integer, ItemStack> getCompasses(Player player) {
    Map<Integer, ItemStack> map = new HashMap<>();
    for (int index = 0; index < player.getInventory().getSize(); index++) {
      ItemStack item = player.getInventory().getItem(index);
      if (item == null || item.getType() != Material.COMPASS) {
        continue;
      }
      map.put(index, item);
    }
    return map;
  }

  public void replaceCompass(Player player, int index, ItemStack itemStack) {
    player.getInventory().setItem(index, itemStack);
  }
}
