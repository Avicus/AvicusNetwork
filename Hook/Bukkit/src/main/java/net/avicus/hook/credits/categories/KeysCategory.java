package net.avicus.hook.credits.categories;

import net.avicus.hook.credits.CategoryMenu;
import net.avicus.hook.credits.GadgetRanksRequirement;
import net.avicus.hook.credits.GadgetStore;
import net.avicus.magma.module.gadgets.crates.KeyGadget;
import net.avicus.magma.module.gadgets.crates.TypeManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KeysCategory extends CategoryMenu {

  public KeysCategory(Player player, GadgetStore store, int index) {
    super(player, store, index);

    if (TypeManager.hasType("alpha")) {
      add(new KeyGadget(TypeManager.getType("alpha")), 1500);
    }

    if (TypeManager.hasType("beta")) {
      add(new KeyGadget(TypeManager.getType("beta")), 2000,
          new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    }

    if (TypeManager.hasType("gamma")) {
      add(new KeyGadget(TypeManager.getType("gamma")), 2500,
          new GadgetRanksRequirement("Emerald", "Diamond"));
    }
  }

  @Override
  public ItemStack getItemStack() {
    ItemStack stack = new ItemStack(Material.FEATHER);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(ChatColor.RED + "Crate Keys");

    stack.setItemMeta(meta);
    return stack;
  }
}
