package net.avicus.hook.gadgets.backpack;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import net.avicus.magma.Magma;
import net.avicus.magma.module.gadgets.GadgetContext;
import net.avicus.magma.module.gadgets.Gadgets;
import net.avicus.magma.util.menu.PaginatedInventory;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BackpackMenu extends PaginatedInventory {

  private static final int ROWS = 4;
  private static final int SIZE = ROWS * 9;

  private final Gadgets gadgets;
  private final BackpackTrashItem trashItem;

  @Getter
  @Setter
  private boolean trashEnabled;

  public BackpackMenu(Player player) {
    super(player, "Backpack", ROWS);
    this.gadgets = Magma.get().getMm().get(Gadgets.class);
    super.setPaginatedItems(generateGadgetItems());
    this.trashItem = new BackpackTrashItem(this, player, SIZE - 5);

    refreshPage();
  }

  public static ItemStack createBackpackOpener(Player player) {
    ItemStack stack = new ItemStack(Material.CHEST);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(ChatColor.GOLD + "Backpack");

    stack.setItemMeta(meta);
    return stack;
  }

  public static boolean isBackpackOpener(ItemStack stack) {
    return stack != null &&
        stack.hasItemMeta() &&
        stack.getItemMeta().hasDisplayName() &&
        stack.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Backpack");
  }

  @Override
  public void refreshPage() {
    super.refreshPage();
    // Trash
    add(this.trashItem);
  }

  public void refreshGadgetItems() {
    this.getPaginator().setCollection(generateGadgetItems());
    refreshPage();
  }

  private Collection<InventoryMenuItem> generateGadgetItems() {
    List<GadgetContext> gadgets = this.gadgets.getGadgets(this.getPlayer().getUniqueId());
    gadgets.sort(new Comparator<GadgetContext>() {
      @Override
      public int compare(GadgetContext o1, GadgetContext o2) {
        return o1.getManager().getType().compareTo(o2.getManager().getType());
      }
    });

    return gadgets.stream()
        .map(gadget -> new BackpackGadgetItem(this, super.getPlayer(), gadget))
        .collect(Collectors.toList());
  }
}
