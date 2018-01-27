package net.avicus.hook.credits.categories;

import net.avicus.hook.credits.CategoryMenu;
import net.avicus.hook.credits.GadgetRanksRequirement;
import net.avicus.hook.credits.GadgetStore;
import net.avicus.hook.gadgets.types.trail.TrailGadget;
import net.avicus.hook.gadgets.types.trail.TrailType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TrailCategory extends CategoryMenu {

  public TrailCategory(Player player, GadgetStore store, int index) {
    super(player, store, index);

    add(new TrailGadget(TrailType.PORTAL), 5000);
    add(new TrailGadget(TrailType.RUNES), 6000);
    add(new TrailGadget(TrailType.CRITICAL), 7000);
    add(new TrailGadget(TrailType.CRITICAL_MAGIC), 8000);
    add(new TrailGadget(TrailType.NOTE), 10000);
    add(new TrailGadget(TrailType.SPELL), 12500);
    add(new TrailGadget(TrailType.WITCH), 17500,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    add(new TrailGadget(TrailType.SLIME), 25000,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    add(new TrailGadget(TrailType.FIRE), 30000,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    add(new TrailGadget(TrailType.EMBER), 50000, new GadgetRanksRequirement("Emerald", "Diamond"));
    add(new TrailGadget(TrailType.HEART), 75000, new GadgetRanksRequirement("Diamond"));
    add(new TrailGadget(TrailType.RAINBOW), 100000, new GadgetRanksRequirement("Diamond"));
  }

  @Override
  public ItemStack getItemStack() {
    ItemStack stack = new ItemStack(Material.PUMPKIN_SEEDS);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Trails");

    stack.setItemMeta(meta);
    return stack;
  }
}
