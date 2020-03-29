package net.avicus.atlas.module.loadouts.modmenu;

import net.avicus.atlas.module.observer.menu.ObserverMenu;
import net.avicus.atlas.util.ScopableItemStack;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.menu.IndexedMenuItem;
import net.avicus.compendium.menu.inventory.ClickableInventoryMenuItem;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ModMenuItem implements ClickableInventoryMenuItem, IndexedMenuItem,
    InventoryMenuItem {

  private static final ItemStack EMPTY = new ItemStack(Material.BARRIER, 1);
  private final Player viewer;
  private final int index;
  private final LoadoutModificationMenu parent;
  private final ScopableItemStack original;
  private ScopableItemStack current;

  public ModMenuItem(Player viewer, int index,
      LoadoutModificationMenu parent, ScopableItemStack original) {
    this.viewer = viewer;
    this.index = index;
    this.parent = parent;
    this.original = original;
    this.current = original;
  }

  @Override
  public ItemStack getItemStack() {
    if (this.current == null)
      return EMPTY;

    return this.current.getItemStack(this.viewer);
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

  @Override
  public void onClick(InventoryClickEvent event) {
    if (event.getClick() == ClickType.RIGHT) {
      this.current = this.original;
      this.parent.update(false);
      return;
    }

    if (event.getClick() == ClickType.LEFT) {

    }
  }
}
