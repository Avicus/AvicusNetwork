package net.avicus.atlas.module.kits.menu;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.avicus.atlas.module.kits.Kit;
import net.avicus.atlas.module.kits.KitsModule;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.menu.inventory.InventoryMenu;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import net.avicus.magma.util.Inventories;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitMenu extends InventoryMenu {

  private KitMenu(KitsModule module, Player player) {
    super(player,
        Messages.UI_KITS.with(ChatColor.RED).render(player).toLegacyText(),
        Inventories.rowCount(module.getKits(player).size()),
        createContents(module, player));
  }

  public static KitMenu create(KitsModule module, Player player) {
    return new KitMenu(module, player);
  }

  private static List<InventoryMenuItem> createContents(KitsModule module, Player player) {
    List<InventoryMenuItem> contents = Lists.newArrayList();

    for (Kit kit : module.getKits(player)) {
      contents.add(KitMenuItem.of(module, kit, player));
    }

    return contents;
  }

  public static ItemStack create(Player player) {
    final ItemStack stack = new ItemStack(Material.BLAZE_ROD);
    final ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(
        Messages.UI_KIT_MENU.with(ChatColor.GOLD).render(player).toLegacyText());
    meta.setLore(Collections.singletonList(ChatColor.BLACK + "Kit Menu"));
    stack.setItemMeta(meta);
    return stack;
  }

  public static boolean matches(ItemStack stack) {
    if (stack == null) {
      return false;
    }

    final ItemMeta meta = stack.getItemMeta();
    return meta.hasLore() && meta.getLore().contains(ChatColor.BLACK + "Kit Menu");
  }
}
