package net.avicus.hook.credits;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.avicus.compendium.menu.IndexedMenuItem;
import net.avicus.compendium.menu.inventory.InventoryIndexer;
import net.avicus.compendium.menu.inventory.InventoryMenu;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;

public class CategoryIndexer implements InventoryIndexer {

  @Override
  public Map<Integer, InventoryMenuItem> getIndices(InventoryMenu inventoryMenu,
      Collection<InventoryMenuItem> collection) {
    Map<Integer, InventoryMenuItem> result = new HashMap<>();

    List<GadgetItem> gadgets = new ArrayList<>();

    for (InventoryMenuItem item : collection) {
      if (item instanceof IndexedMenuItem) {
        result.put(((IndexedMenuItem) item).getIndex(), item);
      } else if (item instanceof GadgetItem) {
        gadgets.add((GadgetItem) item);
      }
    }

    gadgets.sort((o1, o2) -> o1.discountedPrice() - o2.discountedPrice());

    for (int i = 0; i < gadgets.size(); i++) {
      result.put(i, gadgets.get(i));
    }

    return result;
  }
}
