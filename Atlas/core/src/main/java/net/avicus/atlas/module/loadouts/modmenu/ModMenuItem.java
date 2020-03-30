package net.avicus.atlas.module.loadouts.modmenu;

import lombok.Getter;
import net.avicus.atlas.util.ScopableItemStack;
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
  private final int menuIndex;
  private final LoadoutModificationMenu parent;
  private final ScopableItemStack original;
  private final int slot;
  @Getter
  private ScopableItemStack current;

  public ModMenuItem(Player viewer, int menuIndex,
      LoadoutModificationMenu parent, ScopableItemStack original, int slot) {
    this.viewer = viewer;
    this.menuIndex = menuIndex;
    this.parent = parent;
    this.original = original;
    this.slot = slot;
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
    return this.menuIndex;
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    if (event.getClick() == ClickType.RIGHT) {
      update(this.original);
      return;
    }

    if (event.getClick() == ClickType.LEFT) {
      if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
        update(null);
        return;
      }

      update(new ScopableItemStack(this.parent.getMatch(), event.getCursor().clone()));
    }
  }

  private void update(ScopableItemStack newCurrent) {
    if (newCurrent == null) {
      this.parent.getLoadout().getSlotedItems().remove(this.slot);
    } else {
      this.parent.getLoadout().getSlotedItems().put(this.slot, newCurrent);
    }

    this.current = newCurrent;
    this.parent.update(false);
  }
}
