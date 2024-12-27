package net.avicus.magma.util.menu;

import java.util.Locale;
import net.avicus.compendium.menu.IndexedMenuItem;
import net.avicus.compendium.menu.inventory.ClickableInventoryMenuItem;
import net.avicus.compendium.menu.inventory.StaticInventoryMenuItem;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PageItem extends StaticInventoryMenuItem implements IndexedMenuItem,
    ClickableInventoryMenuItem {

  private static final char LEFT_ARROW = '❰';
  private static final char RIGHT_ARROW = '❱';

  private final PaginatedInventory menu;
  private final Player player;
  private final boolean direction;  // negative is left, positive is right
  private final int index;

  public PageItem(PaginatedInventory menu, Player player, boolean direction, int index) {
    this.menu = menu;
    this.player = player;
    this.direction = direction;
    this.index = index;
  }

  @Override
  public ItemStack getItemStack() {
    StringBuilder name = new StringBuilder();
    name.append(ChatColor.GRAY);

    if (this.direction) {
      name.append(MagmaTranslations.GUI_PAGE_NEXT.with().render(this.player).toPlainText());
      name.append(" ");
      name.append(RIGHT_ARROW);
    } else {
      name.append(LEFT_ARROW);
      name.append(" ");
      name.append(MagmaTranslations.GUI_PAGE_PREV.with().render(this.player).toPlainText());
    }

    ItemStack stack = new ItemStack(Material.EMPTY_MAP);
    ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(name.toString());
    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public int getIndex() {
    return this.index;
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    if (this.direction) {
      this.menu.nextPage();
    } else {
      this.menu.prevPage();
    }
  }
}
