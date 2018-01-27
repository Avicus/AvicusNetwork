package net.avicus.atlas.module.loadouts.type;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.module.kits.KitsModule;
import net.avicus.atlas.module.kits.menu.KitMenu;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.atlas.module.shop.ShopModule;
import net.avicus.atlas.module.shop.menu.ShopMenu;
import net.avicus.atlas.util.ScopableItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@Getter
@ToString(callSuper = true)
public class ItemLoadout extends Loadout {

  private final Map<Integer, ScopableItemStack> slotedItems;
  private final List<ScopableItemStack> unslotedItems;

  // Menu
  private final boolean kitMenu;
  @Nullable
  final private int kitMenuSlot;

  // Shop
  private final boolean shopOpener;
  private final int shopSlot;
  private final String shopID;

  public ItemLoadout(boolean force, @Nullable Loadout parent,
      Map<Integer, ScopableItemStack> slotedItems,
      List<ScopableItemStack> unslotedItems, boolean kitMenu, int kitMenuSlot, boolean shopOpener,
      int shopSlot, String shopID) {
    super(force, parent);
    this.slotedItems = slotedItems;
    this.unslotedItems = unslotedItems;
    this.kitMenu = kitMenu;
    this.kitMenuSlot = kitMenuSlot;
    this.shopOpener = shopOpener;
    this.shopSlot = shopSlot;
    this.shopID = shopID;
  }

  @Override
  public void give(Player player, boolean force) {
    final PlayerInventory inventory = player.getInventory();

    // Items
    for (Map.Entry<Integer, ScopableItemStack> entry : this.slotedItems.entrySet()) {
      int slot = entry.getKey();
      ItemStack stack = entry.getValue().getItemStack(player);

      if (slot >= 100) {
        slot -= 100;
        ItemStack[] armor = player.getInventory().getArmorContents();
        if (this.isForce() || force || armor[slot] == null
            || armor[slot].getType() == Material.AIR) {
          armor[slot] = stack;
        }
        player.getInventory().setArmorContents(armor);
      } else {
        if (this.isForce() || force || player.getInventory().getItem(slot) == null) {
          player.getInventory().setItem(slot, stack);
        }
      }
    }

    @Nullable final KitsModule km = Atlas.getMatch().getModule(KitsModule.class).orElse(null);
    if (this.kitMenu && km != null && km.isEnabled(player)) {
      if (this.kitMenuSlot == -1) {
        inventory.addItem(KitMenu.create(player));
      } else {
        inventory.setItem(this.kitMenuSlot, KitMenu.create(player));
      }
    }

    @Nullable final ShopModule sm = Atlas.getMatch().getModule(ShopModule.class).orElse(null);
    if (this.shopOpener && sm != null) {
      if (this.shopSlot == -1) {
        inventory.addItem(ShopMenu.create(player, this.shopID));
      } else {
        inventory.setItem(this.shopSlot, ShopMenu.create(player, this.shopID));
      }
    }

    for (ScopableItemStack item : this.unslotedItems) {
      player.getInventory().addItem(item.getItemStack(player));
    }

    player.updateInventory();
  }
}
