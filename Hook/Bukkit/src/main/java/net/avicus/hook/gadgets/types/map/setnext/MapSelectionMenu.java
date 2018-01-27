package net.avicus.hook.gadgets.types.map.setnext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import net.avicus.hook.gadgets.types.map.AtlasGadget;
import net.avicus.hook.gadgets.types.map.AtlasGadgetManager;
import net.avicus.magma.module.gadgets.EmptyGadgetContext;
import net.avicus.magma.util.menu.PaginatedInventory;
import org.bukkit.entity.Player;

public class MapSelectionMenu extends PaginatedInventory {

  private final AtlasGadget gadget;
  private final EmptyGadgetContext<AtlasGadget> context;

  public MapSelectionMenu(Player player, String title, int rows, AtlasGadget gadget,
      EmptyGadgetContext<AtlasGadget> context) {
    super(player, title, rows);
    this.gadget = gadget;
    this.context = context;
    setPaginatedItems(generateItems());
    refreshPage();
  }

  private Collection<InventoryMenuItem> generateItems() {
    List<InventoryMenuItem> items = new ArrayList<>();
    ((AtlasGadgetManager) this.gadget.getManager()).getPossibleMaps()
        .forEach(m -> items.add(new MapItem(m, this.getPlayer(), this.gadget, this.context)));
    return items;
  }

}
