package net.avicus.magma.network.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.avicus.compendium.config.Config;
import net.avicus.compendium.menu.inventory.InventoryMenu;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import net.avicus.magma.Magma;
import net.avicus.magma.MagmaConfig;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.ServerGroup;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ServerMenu extends InventoryMenu {

  private BukkitTask task;  // repeating update task

  private ServerMenu(Player player, String title, int rows, Collection<InventoryMenuItem> items) {
    super(player, title, rows, items);
  }

  /**
   * Creates a typical server menu from the config.yml file.
   *
   * @return The server menu.
   */
  public static ServerMenu fromConfig(Player player) {
    return new ServerMenu(player, createTitle(player), MagmaConfig.Server.Gui.getRows(),
        itemsForPlayerFromConfig(player));
  }

  private static String createTitle(Player player) {
    return MagmaTranslations.GUI_SERVER_TITLE.with(ChatColor.DARK_GRAY)
        .render(player).toLegacyText();
  }

  private static Collection<InventoryMenuItem> itemsForPlayerFromConfig(Player player) {
    List<InventoryMenuItem> items = new ArrayList<>();

    List<Config> slots = MagmaConfig.Server.Gui.getSlots();
    for (Config config : slots) {
      int index = Integer.parseInt(String.valueOf(config.get("index")));
      if (config.getData().containsKey("server")) {
        String serverName = String.valueOf(config.get("server"));
        Server server = Servers.getCachedServer(serverName).orElse(null);
        if (server != null) {
          items.add(
              new ServerItem(player, index, server, Optional.of(new ItemStack(Material.WATCH))));
        }
      } else if (config.getData().containsKey("group")) {
        String groupName = String.valueOf(config.get("group"));
        ServerGroup group = Servers.getCachedServerGroup(groupName).orElse(null);

        if (group != null) {
          List<Integer> serverIds = Servers.getCachedServerGroupMembers(group);
          items.add(new ServerGroupItem(player, index, group, serverIds));
        }
      }
    }

    return items;
  }

  public static ServerMenu fromServers(Player player, List<Server> servers) {
    // Remove permissible servers
    servers = servers.stream().filter((s) -> !s.isPermissible()).collect(Collectors.toList());

    // Alphabetical
    servers.sort((s1, s2) -> s1.getName().compareTo(s2.getName()));

    Collection<InventoryMenuItem> items = new ArrayList<>();
    for (int i = 0; i < servers.size(); i++) {
      items.add(new ServerItem(player, i, servers.get(i), Optional.empty()));
    }

    int rows = 1 + (int) Math.ceil((double) items.size() / 9.0);

    items.add(new ServerMenuItem(player, rows * 9 - 5));

    return new ServerMenu(player, createTitle(player), rows, items);
  }

  public static ServerMenu fromServerIds(Player player, List<Integer> serverIds) {
    List<Server> servers = serverIds.stream()
        .map((id) -> Servers.getCachedServer(id).orElse(null))
        .filter((s) -> s != null)
        .collect(Collectors.toList());
    return fromServers(player, servers);
  }

  @Override
  public void open() {
    super.open();
    this.task = new BukkitRunnable() {
      @Override
      public void run() {
        update(false);
      }
    }.runTaskTimer(Magma.get(), 0, 20);
  }

  @Override
  public void close() {
    super.close();
    if (this.task != null) {
      this.task.cancel();
    }
  }
}
