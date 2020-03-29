package net.avicus.magma.network.server;

import net.avicus.compendium.menu.IndexedMenuItem;
import net.avicus.compendium.menu.inventory.ClickableInventoryMenuItem;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ServerMenuItem implements InventoryMenuItem, IndexedMenuItem,
    ClickableInventoryMenuItem {

  private final Player player;
  private final int index;

  public ServerMenuItem(Player player, int index) {
    this.player = player;
    this.index = index;
  }

  @Override
  public ItemStack getItemStack() {
    ItemStack stack = new ItemStack(Material.BARRIER);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(
        MagmaTranslations.GUI_GENERIC_BACK.with(ChatColor.RED).translate(this.player.getLocale())
            .toLegacyText());

    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public boolean shouldUpdate() {
    return false;
  }

  @Override
  public void onUpdate() {

  }

  @Override
  public void onClick(InventoryClickEvent event) {
    ServerMenu menu = ServerMenu.fromConfig(this.player);
    menu.open();
  }

  @Override
  public int getIndex() {
    return this.index;
  }
}
