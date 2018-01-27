package net.avicus.hook.credits.categories;

import java.util.HashMap;
import java.util.Map;
import net.avicus.hook.credits.CategoryMenu;
import net.avicus.hook.credits.GadgetRanksRequirement;
import net.avicus.hook.credits.GadgetStore;
import net.avicus.hook.gadgets.types.badge.BadgeGadget;
import net.avicus.hook.gadgets.types.badge.BadgeSymbol;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BadgeCategory extends CategoryMenu {

  private final BadgeSymbol symbol;

  public BadgeCategory(Player player, GadgetStore store, int index, BadgeSymbol symbol) {
    super(player, store, index);
    this.symbol = symbol;

    HashMap<ChatColor, Integer> colors = new HashMap<>();

    colors.put(ChatColor.GRAY, 20);
    colors.put(ChatColor.AQUA, 600);
    colors.put(ChatColor.BLUE, 900);
    colors.put(ChatColor.GOLD, 70000);
    colors.put(ChatColor.GREEN, 25000);
    colors.put(ChatColor.YELLOW, 45000);
    colors.put(ChatColor.RED, 35000);
    colors.put(ChatColor.LIGHT_PURPLE, 15000);

    for (Map.Entry<ChatColor, Integer> entry : colors.entrySet()) {
      int price = entry.getValue() + symbol.getPrice(500);
      BadgeGadget gadget = new BadgeGadget(symbol, entry.getKey());
      if (price < 50000) {
        add(gadget, price);
      } else if (price >= 50000 && price < 75000) {
        add(gadget, price, new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
      } else if (price >= 75000 && price <= 100000) {
        add(gadget, price, new GadgetRanksRequirement("Emerald", "Diamond"));
      } else {
        add(gadget, price, new GadgetRanksRequirement("Diamond"));
      }
    }

  }

  @Override
  public ItemStack getItemStack() {
    ItemStack stack = new ItemStack(Material.NETHER_STAR);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(ChatColor.WHITE + this.symbol.toString() + " Badges");

    stack.setItemMeta(meta);
    return stack;
  }

  public static class BadgeCategoryRoot extends CategoryMenu {

    public BadgeCategoryRoot(Player player, GadgetStore store, int index) {
      super(player, store, index);

      int where = 0;
      for (BadgeSymbol symbol : BadgeSymbol.values()) {
        add(new BadgeCategory(player, store, where, symbol));
        where++;
      }
    }

    @Override
    public ItemStack getItemStack() {
      ItemStack stack = new ItemStack(Material.NETHER_STAR);
      ItemMeta meta = stack.getItemMeta();

      meta.setDisplayName(ChatColor.YELLOW + "Badges");

      stack.setItemMeta(meta);
      return stack;
    }
  }
}
