package net.avicus.hook.credits.categories;

import net.avicus.hook.credits.CategoryMenu;
import net.avicus.hook.credits.GadgetRanksRequirement;
import net.avicus.hook.credits.GadgetStore;
import net.avicus.hook.gadgets.types.arrowtrails.ArrowTrailGadget;
import net.avicus.hook.gadgets.types.arrowtrails.ArrowTrailType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ArrowTrailCategory extends CategoryMenu {

  public ArrowTrailCategory(Player player, GadgetStore store, int index) {
    super(player, store, index);

    add(new ArrowTrailGadget(ArrowTrailType.PORTAL), 5000);
    add(new ArrowTrailGadget(ArrowTrailType.RUNES), 6000);
    add(new ArrowTrailGadget(ArrowTrailType.CRITICAL_MAGIC), 8000);
    add(new ArrowTrailGadget(ArrowTrailType.NOTE), 10000);
    add(new ArrowTrailGadget(ArrowTrailType.SPELL), 12500);
    add(new ArrowTrailGadget(ArrowTrailType.WITCH), 17500,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    add(new ArrowTrailGadget(ArrowTrailType.SLIME), 25000,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    add(new ArrowTrailGadget(ArrowTrailType.EMBER), 50000,
        new GadgetRanksRequirement("Emerald", "Diamond"));
    add(new ArrowTrailGadget(ArrowTrailType.RAINBOW), 100000,
        new GadgetRanksRequirement("Diamond"));
  }

  @Override
  public ItemStack getItemStack() {
    ItemStack stack = new ItemStack(Material.ARROW);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(ChatColor.GOLD + "Arrow Trails");

    stack.setItemMeta(meta);
    return stack;
  }
}
