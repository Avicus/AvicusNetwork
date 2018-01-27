package net.avicus.magma.module.gadgets.crates.reveal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.avicus.compendium.menu.IndexedMenuItem;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import net.avicus.magma.module.gadgets.Gadget;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CrateRevealItem implements InventoryMenuItem, IndexedMenuItem {

  private final Player player;
  private final Gadget gadget;
  private final int index;
  private final double likelihood;

  public CrateRevealItem(Player player, Gadget gadget, int index, double likelihood) {
    this.player = player;
    this.gadget = gadget;
    this.index = index;
    this.likelihood = likelihood;
  }

  @Override
  public ItemStack getItemStack() {
    Locale locale = this.player.getLocale();

    ItemStack stack = this.gadget.icon(locale).clone();
    ItemMeta meta = stack.getItemMeta();
    List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

    lore.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------");

    if (this.likelihood <= 0.05) {
      lore.add(MagmaTranslations.GUI_RARITY_EXTREMELY.with(ChatColor.RED).translate(locale)
          .toLegacyText());
    } else if (this.likelihood <= 0.15) {
      lore.add(
          MagmaTranslations.GUI_RARITY_RARE.with(ChatColor.GOLD).translate(locale).toLegacyText());
    } else if (this.likelihood <= 0.4) {
      lore.add(MagmaTranslations.GUI_RARITY_UNCOMMON.with(ChatColor.YELLOW).translate(locale)
          .toLegacyText());
    } else {
      lore.add(MagmaTranslations.GUI_RARITY_COMMON.with(ChatColor.GREEN).translate(locale)
          .toLegacyText());
    }

    meta.setLore(lore);
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
  public int getIndex() {
    return this.index;
  }
}
