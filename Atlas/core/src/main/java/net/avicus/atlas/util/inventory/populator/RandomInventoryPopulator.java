package net.avicus.atlas.util.inventory.populator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * A populator that will randomly put items into inventories.
 */
public class RandomInventoryPopulator implements InventoryPopulator {

  /**
   * Static instance
   */
  public static final RandomInventoryPopulator INSTANCE = new RandomInventoryPopulator();
  /**
   * Random instance
   */
  private static final Random random = new Random();

  /**
   * Constructor
   */
  private RandomInventoryPopulator() {

  }

  @Override
  public void populate(Inventory inventory, List<ItemStack> items) {
    List<Integer> taken = new ArrayList<>();

    for (int i = 0; i < inventory.getSize(); i++) {
      if (inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR) {
        taken.add(i);
      }
    }

    int max = inventory.getSize() - taken.size();

    // Too many items to put into the inventory!
    while (items.size() > max) {
      items.remove(0);
    }

    for (ItemStack stack : items) {
      int selected = -1;
      while (selected < 0 || taken.contains(selected)) {
        selected = random.nextInt(inventory.getSize());
      }
      taken.add(selected);
      inventory.setItem(selected, stack);
    }
  }
}
