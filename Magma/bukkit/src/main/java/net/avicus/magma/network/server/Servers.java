package net.avicus.magma.network.server;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.avicus.magma.Magma;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.ServerCategory;
import net.avicus.magma.database.model.impl.ServerGroup;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.server.ServerStatus.State;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.redis.Redis;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Seconds;

public class Servers {

  private static final JsonParser parser = new JsonParser();
  private static final Duration OUTDATED = Seconds.seconds(10).toStandardDuration();
  private static final List<Server> serverCache = new ArrayList<>();  // server id -> server
  private static final ArrayListMultimap<ServerGroup, Integer> serverGroupCache = ArrayListMultimap
      .create();  // group -> server ids
  private static final Map<Integer, ItemStack> iconCache = new HashMap<>(); // group -> icon
  private static final Map<Integer, ServerStatus> statusCache = new HashMap<>();  // server id -> status

  public static void init(CommandsManagerRegistration cmd) {
    StatusUpdateTask task = new StatusUpdateTask();
    task.start();

    Magma.get().getServer().getPluginManager()
        .registerEvents(new ServerMenuListener(), Magma.get());
    cmd.register(ServerCommands.class);
  }

  public static void updateServerCache(List<Server> updated,
      ArrayListMultimap<ServerGroup, Integer> groups, Map<Integer, ItemStack> icons) {
    serverCache.clear();
    serverCache.addAll(updated);
    serverGroupCache.clear();
    serverGroupCache.putAll(groups);
    iconCache.clear();
    iconCache.putAll(icons);
  }

  public static List<Server> getServerCache() {
    return serverCache;
  }

  public static Optional<Server> getCachedServer(int id) {
    for (Server server : serverCache) {
      if (server.getId() == id) {
        return Optional.of(server);
      }
    }
    return Optional.empty();
  }

  public static Optional<Server> getCachedServer(String name) {
    for (Server server : serverCache) {
      if (server.getName().equalsIgnoreCase(name)) {
        return Optional.of(server);
      }
    }
    return Optional.empty();
  }

  public static Optional<ServerGroup> getCachedServerGroup(int id) {
    return serverGroupCache.keySet().stream().filter((g) -> g.getId() == id).findAny();
  }

  public static Optional<ServerGroup> getCachedServerGroup(String slug) {
    return serverGroupCache.keySet().stream().filter((g) -> g.getSlug().equalsIgnoreCase(slug))
        .findAny();
  }

  public static ItemStack getCachedIcon(ServerGroup group) {
    return iconCache.get((Integer) group.getId());
  }

  public static List<Integer> getCachedServerGroupMembers(ServerGroup group) {
    if (serverGroupCache.containsKey(group)) {
      return new ArrayList<>(serverGroupCache.get(group));
    }

    for (ServerGroup test : serverGroupCache.keySet()) {
      if (test.getId() == group.getId()) {
        return new ArrayList<>(serverGroupCache.get(test));
      }
    }

    return new ArrayList<>();
  }

  /**
   * Sends BungeeCord data to transfer a player to a server.
   *
   * @param ignoreOffline If true, data will be sent regardless of an offline server status.
   * @param message If a messages should be sent to the player.
   * @return If the connection was successful (at least from Bukkit's perspective).
   */
  public static boolean connect(Player player, Server server, boolean ignoreOffline,
      boolean message) {
    // TODO: hacky fix for different DBs
    ignoreOffline =
        ignoreOffline || server.getCategory(Magma.get().database().getServerCategories())
            .map(ServerCategory::getName).orElse("").toLowerCase().contains("dev") ||
            (Magma.get().localCategory() != null && Magma.get().localCategory().getName()
                .toLowerCase().contains("dev"));

    if (!ignoreOffline) {
      ServerStatus status = getStatus(server).orElse(null);

      if (status == null || status.getState() == State.OFFLINE) {
        if (message) {
          player.sendMessage(MagmaTranslations.COMMANDS_SERVER_ERROR_OFFLINE
              .with(ChatColor.RED, server.getName()));
        }
        return false;
      }
    }

    if (message) {
      player.sendMessage(
          MagmaTranslations.COMMANDS_SERVER_CONNECTING.with(ChatColor.GOLD, server.getName()));
    }

    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("Connect");
    out.writeUTF(server.getName());
    player.sendPluginMessage(Magma.get(), "BungeeCord", out.toByteArray());
    return true;
  }

  public static ItemStack createMenuOpener(Player player) {
    ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK);

    ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(
        MagmaTranslations.GUI_SERVER_TITLE.with(ChatColor.GOLD).render(player)
            .toLegacyText());

    // English is stored plainly in order to match properly on right click
    meta.setLore(Collections.singletonList(ChatColor.BLACK + "Server Menu"));
    stack.setItemMeta(meta);

    return stack;
  }

  public static boolean isMenuOpener(ItemStack stack) {
    if (stack == null) {
      return false;
    }

    ItemMeta meta = stack.getItemMeta();

    return meta.hasLore() && meta.getLore().contains(ChatColor.BLACK + "Server Menu");
  }

  public static void setStatus(Server server, ServerStatus status) {
    statusCache.put(server.getId(), status);
  }

  public static Optional<ServerStatus> getStatus(Server server) {
    return getStatus(server.getId());
  }

  public static Optional<ServerStatus> getStatus(int id) {
    return Optional.ofNullable(statusCache.get(id));
  }

  public static Collection<ServerStatus> getAllStatuses() {
    return statusCache.values();
  }

  public static ServerStatus generateStatus() {
    Server server = Magma.get().localServer();
    Instant now = Instant.now();
    List<User> players = Users.list();
    int count = server.getPlayers();
    int spectating = server.getSpectators();
    int max = server.getMaxPlayers();
    State state = State.valueOfOrDefault(server.getState());
    String message = server.getActiveMap();
    JsonObject custom = new JsonObject();

    return new ServerStatus(server, true, now, players, count, spectating, max, state, message,
        custom);
  }

  /**
   * Pushes to redis an updated server status.
   */
  public static void syncUp(Redis redis, ServerStatus status) {
    JsonObject json = status.serialize();
    redis.hset("servers", status.getId() + "", json.toString());
  }

  /**
   * Gets the latest server statuses.
   */
  public static void syncDown(Redis redis) {
    Database mysql = Magma.get().database();

    Map<String, String> map = redis.hgetall("servers");
    for (String id : map.keySet()) {
      try {
        Server server = mysql.getServers().findById(Integer.parseInt(id)).orElse(null);

        if (server == null) {
          continue;
        }

        String raw = map.get(id);
        JsonObject json = parser.parse(raw).getAsJsonObject();
        ServerStatus status = ServerStatus.deserialize(mysql, server, json);

        setStatus(server, status);

        boolean outdated = isOutdated(status);

        if (outdated) {
          syncUp(redis, new ServerStatus(server));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static boolean isOutdated(ServerStatus status) {
    Instant outdated = Instant.now().minus(OUTDATED);
    return status.getTimestamp().isBefore(outdated);
  }
}
