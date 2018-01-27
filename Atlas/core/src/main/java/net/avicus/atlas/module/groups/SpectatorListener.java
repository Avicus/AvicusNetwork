package net.avicus.atlas.module.groups;

import java.util.Collections;
import java.util.Locale;
import net.avicus.atlas.event.player.PlayerSpawnBeginEvent;
import net.avicus.atlas.module.observer.menu.ObserverMenu;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.TextStyle;
import net.avicus.magma.network.server.Servers;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpectatorListener implements Listener {

  private final GroupsModule module;

  public SpectatorListener(GroupsModule module) {
    this.module = module;
  }

  private ItemStack createTeleportDevice(Player player) {
    ItemStack stack = new ItemStack(Material.COMPASS);
    ItemMeta meta = stack.getItemMeta();

    Locale locale = player.getLocale();
    meta.setDisplayName(
        Messages.UI_TELEPORT_DEVICE.with(TextStyle.ofColor(ChatColor.RED)).translate(locale)
            .toLegacyText());
    meta.setLore(Collections.singletonList(
        Messages.UI_TELEPORT_DEVICE_TEXT.with(TextStyle.ofColor(ChatColor.WHITE)).translate(locale)
            .toLegacyText()
    ));

    stack.setItemMeta(meta);
    return stack;
  }

  @EventHandler
  public void onPlayerSpawn(PlayerSpawnBeginEvent event) {
    if (!this.module.getSpectators().equals(event.getGroup())) {
      return;
    }

    if (!event.isGiveLoadout()) {
      return;
    }

    Player player = event.getPlayer();
    player.getInventory().addItem(createTeleportDevice(event.getPlayer()));
    player.getInventory().addItem(ObserverMenu.icon(player));
    player.getInventory().setItem(8, Servers.createMenuOpener(player));
  }


  @EventHandler
  public void playerInteract(final PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_AIR
        && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (ObserverMenu.matches(event.getItem()) && ObserverMenu.canOpen(event.getPlayer())) {
      ObserverMenu.create(event.getPlayer()).open();
    }
  }
}
