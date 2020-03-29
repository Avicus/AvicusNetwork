package net.avicus.atlas.module.loadouts.modmenu;

import java.util.Collections;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.loadouts.type.ItemLoadout;
import net.avicus.atlas.module.observer.menu.item.GameModeItem;
import net.avicus.atlas.module.observer.menu.item.NightVisionItem;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.menu.inventory.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class LoadoutModificationMenu extends InventoryMenu {

  private final ItemLoadout loadout;

  private LoadoutModificationMenu(final Player viewer, ItemLoadout loadout, String id) {
    super(
        viewer,
        ChatColor.GOLD + "Modifying " + id,
        9
    );
    this.loadout = loadout;
    this.update(true);
  }
}
