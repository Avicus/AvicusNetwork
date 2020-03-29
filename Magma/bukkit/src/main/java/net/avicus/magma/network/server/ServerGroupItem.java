package net.avicus.magma.network.server;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.menu.IndexedMenuItem;
import net.avicus.compendium.menu.inventory.ClickableInventoryMenuItem;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.ServerGroup;
import net.avicus.magma.network.server.qp.PlayerRequestHandler;
import net.avicus.magma.network.server.qp.QuickPlay;
import net.avicus.magma.util.Inventories;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ServerGroupItem implements InventoryMenuItem, IndexedMenuItem,
    ClickableInventoryMenuItem {

  public static LocalizableFormat BULLET_POINT = new UnlocalizedFormat("• {0}");
  public static LocalizableFormat ARROW_POINT = new UnlocalizedFormat("▶ {0}");
  private final String PLAYER_COUNT_FORMAT = ChatColor
      .translateAlternateColorCodes('&', "&a{0} &b/ &4{1}");
  private final String PLAYER_COUNT_FORMAT_SPECS = ChatColor
      .translateAlternateColorCodes('&', "&a{0} &b/ &4{1} &7playing &7(&f+{2} &7spectating)");

  private final Player player;
  private final int index;
  private final ServerGroup group;
  private final List<Integer> serverIds;

  public ServerGroupItem(Player player, int index, ServerGroup group, List<Integer> serverIds) {
    this.player = player;
    this.index = index;
    this.group = group;
    this.serverIds = serverIds;
  }

  public int playerCount() {
    int count = 0;
    for (int serverId : this.serverIds) {
      count += Servers.getStatus(serverId).map(ServerStatus::getPlayingCount).orElse(0);
    }
    return count;
  }

  public int spectatingCount() {
    int count = 0;
    for (int serverId : this.serverIds) {
      count += Servers.getStatus(serverId).map(ServerStatus::getSpectatingCount).orElse(0);
    }
    return count;
  }

  public int maxPlayerCount() {
    int count = 0;
    for (int serverId : this.serverIds) {
      count += Servers.getStatus(serverId).map(ServerStatus::getMaxPlayers).orElse(0);
    }
    return count;
  }

  @Override
  public ItemStack getItemStack() {
    Locale locale = this.player.getLocale();

    ItemStack stack = Servers.getCachedIcon(this.group);
    Server server = leftClickServer().orElse(null);

    ItemMeta meta = stack.getItemMeta();

    int players = playerCount();
    int spectating = spectatingCount();
    int maxPlayers = maxPlayerCount();

    stack.setAmount(Inventories.clampedStackAmount(players + spectating));

    String groupDisplay = ChatColor.WHITE.toString() + ChatColor.BOLD + this.group.getName();
    if (server != null) {
      meta.setDisplayName(
          groupDisplay + ChatColor.GOLD + " » " + ChatColor.AQUA + server.getName());
    } else {
      meta.setDisplayName(groupDisplay);
    }

    List<String> lore = new ArrayList<>();
    if (spectating > 0) {
      lore.add(MessageFormat.format(PLAYER_COUNT_FORMAT_SPECS, players, maxPlayers, spectating));
    } else {
      lore.add(MessageFormat.format(PLAYER_COUNT_FORMAT, players, maxPlayers));
    }
    lore.add(ChatColor.GOLD + this.group.getDescription());

    if (Servers.getCachedServerGroupMembers(this.group).size() > 1) {
      TextStyle style = TextStyle.ofColor(ChatColor.YELLOW).bold();
      lore.add(BULLET_POINT.with(MagmaTranslations.GUI_SERVER_CLICK_LEFT.with(ChatColor.YELLOW))
          .translate(locale).toLegacyText());
      lore.add(
          ARROW_POINT.with(MagmaTranslations.GUI_SERVER_CLICK_RIGHT.with(style)).translate(locale)
              .toLegacyText());
    }

    meta.setLore(lore);

    stack.setItemMeta(meta);

    return stack;
  }

  @Override
  public boolean shouldUpdate() {
    return true;
  }

  @Override
  public void onUpdate() {
    // nothing needed
  }

  private Optional<Server> leftClickServer() {
    Optional<PlayerRequestHandler.PlayerRequestMessage> highest = QuickPlay.requests(this.player)
        .stream()
        .filter((msg) -> this.serverIds.contains(msg.getServer().getId()))
        .findFirst();
    return highest.map(PlayerRequestHandler.PlayerRequestMessage::getServer);
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    ClickType type = event.getClick();
    if (type.isLeftClick()) {
      Server server = leftClickServer().orElse(null);

      if (server == null) {
        this.player
            .sendMessage(MagmaTranslations.COMMANDS_SERVER_QUERY_GROUP_NONE.with(ChatColor.RED));
        return;
      }

      Servers.connect(this.player, server, false, true);
    } else if (type.isRightClick()) {
      ServerMenu menu = ServerMenu.fromServerIds(this.player, this.serverIds);
      menu.open();
    }
  }

  @Override
  public int getIndex() {
    return this.index;
  }
}