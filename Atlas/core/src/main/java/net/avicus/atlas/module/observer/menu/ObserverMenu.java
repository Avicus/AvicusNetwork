package net.avicus.atlas.module.observer.menu;

import java.util.Collections;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.observer.menu.item.GameModeItem;
import net.avicus.atlas.module.observer.menu.item.NightVisionItem;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.menu.inventory.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ObserverMenu extends InventoryMenu {

  private ObserverMenu(final Player viewer) {
    super(
        viewer,
        Translations.MODULE_OBSERVER_MENU_TITLE.with(ChatColor.AQUA).render(viewer)
            .toLegacyText(),
        1
    );
    this.add(new GameModeItem(viewer, this, 2));
    this.add(new NightVisionItem(viewer, this, 6));
    this.update(true);
  }

  public static ObserverMenu create(final Player viewer) {
    return new ObserverMenu(viewer);
  }

  public static ItemStack icon(final Player viewer) {
    final ItemStack stack = new ItemStack(Material.TRIPWIRE_HOOK);
    final ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(
        ChatColor.RESET + Translations.MODULE_OBSERVER_MENU_TITLE.with(ChatColor.AQUA)
            .render(viewer).toLegacyText());
    meta.setLore(Collections.singletonList(ChatColor.BLACK + "observer-menu"));
    stack.setItemMeta(meta);
    return stack;
  }

  public static boolean matches(final ItemStack stack) {
    if (stack == null) {
      return false;
    }

    final ItemMeta meta = stack.getItemMeta();
    return meta.hasLore() && meta.getLore().contains(ChatColor.BLACK + "observer-menu");
  }

  public static boolean canOpen(final Player viewer) {
    final Match match = Atlas.getMatch();
    return match != null && match.getRequiredModule(GroupsModule.class).isObserving(viewer);

  }
}
