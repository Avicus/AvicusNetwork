package net.avicus.magma.network.server;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.avicus.compendium.menu.IndexedMenuItem;
import net.avicus.compendium.menu.inventory.ClickableInventoryMenuItem;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.util.Inventories;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ServerItem implements InventoryMenuItem, IndexedMenuItem, ClickableInventoryMenuItem {

  private final String PLAYER_COUNT_FORMAT = ChatColor
      .translateAlternateColorCodes('&', "&a{0} &b/ &4{1}");
  private final String PLAYER_COUNT_FORMAT_SPECS = ChatColor
      .translateAlternateColorCodes('&', "&a{0} &b/ &4{1} &7playing &7(&f+{2} &7spectating)");
  private final int index;
  private final Server server;
  private final Optional<ItemStack> icon;
  private Player player;
  private ServerStatus lastStatus;

  public ServerItem(Player player, int index, Server server, Optional<ItemStack> icon) {
    this.player = player;
    this.index = index;
    this.server = server;
    this.icon = icon;
    this.lastStatus = Servers.getStatus(server).orElse(new ServerStatus(server));
  }

  @Override
  public ItemStack getItemStack() {
    ItemStack stack;
    ServerStatus status = this.lastStatus;

    if (this.icon.isPresent()) {
      stack = this.icon.get();
    } else {
      stack = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
      SkullMeta meta = (SkullMeta) stack.getItemMeta();

      stack.setAmount(
          Inventories.clampedStackAmount(status.getPlayingCount() + status.getSpectatingCount()));
      meta.setOwner(null, UUID.randomUUID(), status.getState().getSkin());
      stack.setItemMeta(meta);
    }
    ItemMeta meta = stack.getItemMeta();
    List<String> lore = new ArrayList<>();
    if (status.getSpectatingCount() > 0) {
      lore.add(MessageFormat
          .format(PLAYER_COUNT_FORMAT_SPECS, status.getPlayingCount(), status.getMaxPlayers(),
              status.getSpectatingCount()));
    } else {
      if (status.getPlayingCount() + status.getSpectatingCount() > 0) {
        lore.add(MessageFormat
            .format(PLAYER_COUNT_FORMAT, status.getPlayingCount(), status.getMaxPlayers()));
      } else {
        if (status.getMaxPlayers() > 0) {
          lore.add(MessageFormat
              .format(PLAYER_COUNT_FORMAT, status.getPlayers().size(), status.getMaxPlayers()));
        } else {
          lore.add(MessageFormat.format(PLAYER_COUNT_FORMAT, status.getPlayers().size(),
              Math.max(100, status.getMaxPlayers())));
        }
      }
    }
    if (status.getMessage().isPresent()) {
      lore.add(status.getState().getColor() + status.getMessage().get());
    }

    meta.setDisplayName(status.getState().getColor() + this.server.getName());
    meta.setLore(lore);

    stack.setItemMeta(meta);

    return stack;
  }

  @Override
  public boolean shouldUpdate() {
    ServerStatus before = this.lastStatus;
    ServerStatus after = Servers.getStatus(this.server).orElse(new ServerStatus(this.server));

    this.lastStatus = after;

    // Only update if before does not equal after
    return !Objects.equals(before, after);
  }

  @Override
  public void onUpdate() {
    // nothing needed
  }

  @Override
  public void onClick(ClickType type) {
    Servers.connect(this.player, this.server, false, true);
  }

  @Override
  public int getIndex() {
    return this.index;
  }
}
