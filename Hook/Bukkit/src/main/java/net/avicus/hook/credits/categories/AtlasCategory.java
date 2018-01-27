package net.avicus.hook.credits.categories;

import net.avicus.hook.credits.CategoryMenu;
import net.avicus.hook.credits.GadgetRanksRequirement;
import net.avicus.hook.credits.GadgetStore;
import net.avicus.hook.gadgets.types.map.setnext.SetNextGadget;
import net.avicus.hook.gadgets.types.map.startvote.StartVoteGadget;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AtlasCategory extends CategoryMenu {

  public AtlasCategory(Player player, GadgetStore store, int index) {
    super(player, store, index);

    add(new SetNextGadget(), 22000, new GadgetRanksRequirement("diamond"));
    add(new StartVoteGadget(), 15000, new GadgetRanksRequirement("emerald", "diamond"));
  }

  @Override
  public ItemStack getItemStack() {
    ItemStack stack = new ItemStack(Material.MAP);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(ChatColor.RED + "Game Modifications");

    stack.setItemMeta(meta);
    return stack;
  }
}
