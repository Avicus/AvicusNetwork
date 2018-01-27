package net.avicus.magma.item;

import java.util.Iterator;
import javax.annotation.Nullable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This prevents items from being moved/shared based on tags.
 */
public class LockingSharingListener implements Listener {

  protected static final ItemTag.Boolean LOCKED = new ItemTag.Boolean("locked", false);
  protected static final ItemTag.Boolean UN_SHAREABLE = new ItemTag.Boolean("un-shareable", false);

  private boolean isLocked(@Nullable ItemStack item) {
    return item != null && LOCKED.get(item);
  }

  private boolean unShareable(@Nullable ItemStack item) {
    return item != null && (isLocked(item) || UN_SHAREABLE.get(item));
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onInventoryClick(final InventoryClickEvent event) {
    if (event instanceof InventoryCreativeEvent) {
      return;
    }
    ;

    // Break out of the switch if the action will move a locked item, otherwise return
    switch (event.getAction()) {
      case HOTBAR_SWAP:
      case HOTBAR_MOVE_AND_READD:
        // These actions can move up to two stacks. Check the hotbar stack,
        // and then fall through to check the stack under the cursor.
        if (isLocked(event.getInventory().getItem(event.getHotbarButton()))) {
          break;
        }
      case PICKUP_ALL:
      case PICKUP_HALF:
      case PICKUP_SOME:
      case PICKUP_ONE:
      case SWAP_WITH_CURSOR:
      case MOVE_TO_OTHER_INVENTORY:
      case DROP_ONE_SLOT:
      case DROP_ALL_SLOT:
      case COLLECT_TO_CURSOR:
        if (isLocked(event.getCurrentItem())) {
          break;
        }
      default:
        return;
    }

    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onDropItem(PlayerDropItemEvent event) {
    if (isLocked(event.getItemDrop().getItemStack())) {
      event.setCancelled(true);
    } else if (unShareable(event.getItemDrop().getItemStack())) {
      event.getItemDrop().remove();
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onDeath(PlayerDeathEvent event) {
    for (Iterator<ItemStack> iterator = event.getDrops().iterator(); iterator.hasNext(); ) {
      if (unShareable(iterator.next())) {
        iterator.remove();
      }
      ;
    }
  }
}
