package net.avicus.hook.credits;

import java.util.Arrays;
import java.util.Locale;
import net.avicus.compendium.menu.IndexedMenuItem;
import net.avicus.compendium.menu.inventory.ClickableInventoryMenuItem;
import net.avicus.compendium.menu.inventory.InventoryMenu;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import net.avicus.compendium.menu.inventory.StaticInventoryMenuItem;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.Gadget;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class CategoryMenu extends InventoryMenu implements InventoryMenuItem,
    IndexedMenuItem, ClickableInventoryMenuItem {

  private static final int ROWS = 6;

  private final Player player;
  private final GadgetStore store;
  private final int index;

  public CategoryMenu(Player player, GadgetStore store, int index) {
    super(player, createTitle(player), ROWS, new CategoryIndexer());
    this.player = player;
    this.store = store;
    this.index = index;

    add(new BackItem(player, store, ROWS * 9 - 5));
  }

  private static String createTitle(Player player) {
    return Messages.UI_GADGET_STORE.with(ChatColor.DARK_GRAY).render(player)
        .toLegacyText();
  }

  public void onRightClick(GadgetItem item, User clicked, Player player) {
    item.handleClick(clicked, player);
  }

  public void add(Gadget gadget, int price, GadgetPurchaseRequirement... requirements) {
    add(new GadgetItem(this, this.player, gadget, price, Arrays.asList(requirements)));
  }

  @Override
  public final int getIndex() {
    return this.index;
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    this.open();
  }

  @Override
  public final boolean shouldUpdate() {
    return false;
  }

  @Override
  public final void onUpdate() {

  }

  private class BackItem extends StaticInventoryMenuItem implements ClickableInventoryMenuItem,
      IndexedMenuItem {

    private final Player p;
    private final GadgetStore store;
    private final int index;

    public BackItem(Player player, GadgetStore store, int index) {
      this.p = player;
      this.store = store;
      this.index = index;
    }

    @Override
    public ItemStack getItemStack() {
      ItemStack stack = new ItemStack(Material.INK_SACK, 1, (byte) 10);
      ItemMeta meta = stack.getItemMeta();

      meta.setDisplayName(
          Messages.UI_BACK.with(ChatColor.GREEN).render(this.p).toLegacyText());

      stack.setItemMeta(meta);
      return stack;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
      this.store.open();
    }

    @Override
    public int getIndex() {
      return this.index;
    }
  }
}
