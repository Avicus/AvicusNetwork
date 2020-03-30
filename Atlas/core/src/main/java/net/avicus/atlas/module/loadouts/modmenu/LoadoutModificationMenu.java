package net.avicus.atlas.module.loadouts.modmenu;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.Getter;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.loadouts.type.ItemLoadout;
import net.avicus.compendium.menu.inventory.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter(AccessLevel.PROTECTED)
public final class LoadoutModificationMenu extends InventoryMenu {

  private final Match match;
  private final ItemLoadout loadout;

  public LoadoutModificationMenu(final Player viewer, ItemLoadout loadout, Match match, String id) {
    super(
        viewer,
        ChatColor.GOLD + "Modifying " + id,
        5
    );
    this.loadout = loadout;
    this.match = match;
    loadout.getSlotedItems().forEach((s, i) -> {
      // Armor
      int index = (s >= 100 ? s - 100 + 36 : s);

      add(new ModMenuItem(viewer, index, this, i, s));
    });

    List<Integer> openSlots = IntStream.rangeClosed(1, 45).boxed().collect(Collectors.toList());
    getItems().stream().map(item -> ((ModMenuItem) item).getIndex())
        .forEach(openSlots::remove);
    for (Integer slot : openSlots) {
      add(new ModMenuItem(viewer, slot, this, null, slot));
    }
    this.update(true);
  }
}
