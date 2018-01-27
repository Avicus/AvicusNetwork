package net.avicus.atlas.module.invsee;

import java.util.HashMap;
import java.util.Map;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.groups.GroupsModule;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InvSeeModule implements Module {

  private final Match match;
  private final Map<Player, TrackedInventory> opened;
  private final InvSeeTask task;

  public InvSeeModule(Match match) {
    this.match = match;
    this.opened = new HashMap<>();
    this.task = new InvSeeTask(this.opened);
  }

  @Override
  public void open() {
    this.task.repeat(0, 5);
  }

  @Override
  public void close() {
    this.task.cancel0();
  }

  /**
   * Helper method for observer checks.
   */
  private boolean isObserving(Player player) {
    // Only observers, not dead players.
    return this.match.getRequiredModule(GroupsModule.class).isObserving(player);
  }

  /**
   * Creates a tracked inventory and opens up the view.
   */
  private void trackInventory(Player player, Inventory inventory, String name) {
    Inventory view;
    if (inventory.getType() == InventoryType.CHEST) {
      view = Bukkit.createInventory(inventory.getHolder(), inventory.getSize(), name);
    } else if (inventory.getType() == InventoryType.PLAYER) {
      view = Bukkit.createInventory(inventory.getHolder(), 45, name);
    } else {
      view = Bukkit.createInventory(inventory.getHolder(), inventory.getType(), name);
    }

    TrackedInventory tracked = new TrackedInventory(player, inventory, view);
    tracked.open();
    this.opened.put(player, tracked);
  }

  @EventHandler
  public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    if (!(event.getRightClicked() instanceof Player)) {
      return;
    }

    if (!isObserving(event.getPlayer())) {
      return;
    }

    Player player = event.getPlayer();
    Player target = (Player) event.getRightClicked();

    if (isObserving(target)) {
      return;
    }

    trackInventory(player, target.getInventory(), target.getName());
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (!isObserving(event.getPlayer())) {
      return;
    }

    Block block = event.getClickedBlock();

    // Matches all blocks that persist with items
    if (block.getState() instanceof InventoryHolder) {
      InventoryHolder container = (InventoryHolder) block.getState();
      trackInventory(event.getPlayer(), container.getInventory(), "");
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }

    if (this.opened.keySet().contains(event.getWhoClicked())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    this.opened.remove(event.getPlayer());
  }
}
