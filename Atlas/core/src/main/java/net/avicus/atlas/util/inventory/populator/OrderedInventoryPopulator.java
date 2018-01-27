package net.avicus.atlas.util.inventory.populator;

import java.util.List;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * A populator that adds items to an inventory just as they are in a list.
 */
public class OrderedInventoryPopulator implements InventoryPopulator {

  /**
   * Static instance
   */
  public static OrderedInventoryPopulator INSTANCE = new OrderedInventoryPopulator();

  /**
   * Constructor.
   */
  private OrderedInventoryPopulator() {

  }

  @Override
  public void populate(Inventory inventory, List<ItemStack> items) {
    for (int i = 0; i < items.size() && i < inventory.getSize(); i++) {
      inventory.setItem(i, items.get(i));
    }
  }
}
