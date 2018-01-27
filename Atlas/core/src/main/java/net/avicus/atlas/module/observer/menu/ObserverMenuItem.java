package net.avicus.atlas.module.observer.menu;

import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.menu.IndexedMenuItem;
import net.avicus.compendium.menu.inventory.ClickableInventoryMenuItem;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import org.bukkit.entity.Player;

public abstract class ObserverMenuItem implements ClickableInventoryMenuItem, IndexedMenuItem,
    InventoryMenuItem {

  protected static final int MAX_LENGTH = 50;
  protected static final UnlocalizedFormat TWO_PART_FORMAT = new UnlocalizedFormat("{0}: {1}");
  protected final Player viewer;
  protected final ObserverMenu parent;
  protected final int index;

  protected ObserverMenuItem(final Player viewer, final ObserverMenu parent, final int index) {
    this.viewer = viewer;
    this.parent = parent;
    this.index = index;
  }

  @Override
  public boolean shouldUpdate() {
    return true;
  }

  @Override
  public void onUpdate() {
  }

  @Override
  public int getIndex() {
    return this.index;
  }
}
