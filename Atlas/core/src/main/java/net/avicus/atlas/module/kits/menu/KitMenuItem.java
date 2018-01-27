package net.avicus.atlas.module.kits.menu;

import java.util.Collections;
import java.util.Locale;
import net.avicus.atlas.module.kits.Kit;
import net.avicus.atlas.module.kits.KitsModule;
import net.avicus.compendium.menu.inventory.ClickableInventoryMenuItem;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitMenuItem implements ClickableInventoryMenuItem, InventoryMenuItem {

  private final KitsModule module;
  private final Kit kit;
  private final Player player;
  private ItemStack stack;

  private KitMenuItem(KitsModule module, Kit kit, Player player) {
    this.module = module;
    this.kit = kit;
    this.player = player;
  }

  public static KitMenuItem of(KitsModule module, Kit kit, Player player) {
    return new KitMenuItem(module, kit, player);
  }

  @Override
  public void onClick(ClickType type) {
    this.module.setUpcomingKit(this.player, this.kit);
    this.kit.displaySelectedMessage(this.player);
  }

  @Override
  public ItemStack getItemStack() {
    if (this.stack == null) {
      final Locale locale = this.player.getLocale();
      this.stack = this.kit.getIcon().getBaseItemStack().clone();
      ItemMeta meta = this.stack.getItemMeta();
      meta.setDisplayName(ChatColor.RESET + this.kit.getName().translate(locale));
      this.kit.getDescription().ifPresent(
          description -> meta.setLore(Collections.singletonList(description.translate(locale))));
      meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES);
      this.stack.setItemMeta(meta);
    }

    return this.stack;
  }

  @Override
  public boolean shouldUpdate() {
    return false;
  }

  @Override
  public void onUpdate() {
  }
}
