package net.avicus.magma.util.menu;

import java.util.ArrayList;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import net.avicus.compendium.Paginator;
import net.avicus.compendium.menu.inventory.InventoryMenu;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import org.bukkit.entity.Player;

public abstract class PaginatedInventory extends InventoryMenu {

  @Getter
  private final Player player;
  @Getter
  private final Paginator<InventoryMenuItem> paginator;

  private final PageItem prevPageItem;
  private final PageItem nextPageItem;

  @Getter
  @Setter
  private int page;

  public PaginatedInventory(Player player, String title, int rows) {
    super(player, title, rows);
    this.player = player;
    this.paginator = new Paginator<>(new ArrayList<>(), (rows - 1) * 9);
    this.prevPageItem = new PageItem(this, player, false, rows * 9 - 9);
    this.nextPageItem = new PageItem(this, player, true, rows * 9 - 1);
  }

  public void refreshPage() {
    removeAll(getItems());

    // Gadgets
    if (this.getPaginator().hasPage(this.getPage())) {
      add(this.getPaginator().getPage(this.getPage()));
    }

    // Check if there are gadgets, but on a page that doesn't have any items
    if (!this.getPaginator().getCollection().isEmpty() && getItems().isEmpty()) {
      setPage(0);
      refreshPage();
      return;
    }
    addPageItems();
  }

  public void setPaginatedItems(Collection<InventoryMenuItem> items) {
    this.paginator.setCollection(items);
  }

  public void addPageItems() {
    if (hasPrevPage()) {
      add(this.prevPageItem);
    }
    if (hasNextPage()) {
      add(this.nextPageItem);
    }
  }

  public void prevPage() {
    if (!hasPrevPage()) {
      return;
    }

    this.page--;
    refreshPage();
  }

  public void nextPage() {
    if (!hasNextPage()) {
      return;
    }

    this.page++;
    refreshPage();
  }

  public boolean hasPrevPage() {
    return this.paginator.hasPage(this.page - 1);
  }

  public boolean hasNextPage() {
    return this.paginator.hasPage(this.page + 1);
  }
}
