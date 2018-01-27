package net.avicus.atlas.util.inventory.populator;

import java.util.List;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * An inventory populator handles the population of inventories.
 */
public interface InventoryPopulator {

  void populate(Inventory inventory, List<ItemStack> items);
}
