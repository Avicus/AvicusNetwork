package net.avicus.atrio;


import static net.avicus.atrio.ConfigUtils.parseBounded;
import static net.avicus.atrio.ConfigUtils.vecFromString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import net.avicus.compendium.config.Config;
import net.avicus.compendium.config.inject.ConfigKey;
import net.avicus.compendium.config.inject.ConfigPath;
import net.avicus.compendium.utils.Strings;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.util.region.BoundedRegion;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class AtrioConfig {

  @Getter
  @ConfigKey(key = "update-interval")
  private static int updateInterval;

  @Getter
  @ConfigKey(key = "announce-forum")
  private static int announceForum;

  @ConfigKey
  private static List<Config> signs;
  @ConfigKey
  private static List<Config> portals;
  @ConfigKey
  private static List<Config> pads;
  @ConfigKey
  private static List<Config> presents;
  @ConfigKey
  private static Config spawn;

  public static List<ServerSign> getSigns() {
    List<ServerSign> serverSigns = new ArrayList<>();

    for (Config list : signs) {
      try {
        String[] locationParts = list.getString("location").split(",");
        double x = Double.parseDouble(locationParts[0]);
        double y = Double.parseDouble(locationParts[1]);
        double z = Double.parseDouble(locationParts[2]);
        Vector vector = new Vector(x, y, z);

        Optional<Server> server = Magma.get().database().getServers()
            .findByName(list.getAsString("server"));

        if (!server.isPresent()) {
          Bukkit.getConsoleSender()
              .sendMessage("Failed to find server: " + list.getAsString("server"));
          continue;
        }

        serverSigns.add(new ServerSign(server.get(), vector));
      } catch (Exception e) {
        Bukkit.getConsoleSender().sendMessage("Failed to parse server sign: " + list.toString());
        e.printStackTrace();
      }
    }

    return serverSigns;
  }

  public static List<Portal> getPortals() {
    List<Portal> parsedPortals = new ArrayList<>();
    DateTimeFormatter f = DateTimeFormat.forPattern("MM-dd-yy HH:mm");

    for (Config list : portals) {
      try {
        Portal.PortalType type = list.getAsEnum("type", Portal.PortalType.class);

        Optional<DateTime> open = Optional.empty();
        Optional<DateTime> close = Optional.empty();

        if (list.get("open") != null) {
          open = Optional.of(f.parseDateTime(list.getAsString("open")));
        }

        if (list.get("close") != null) {
          close = Optional.of(f.parseDateTime(list.getAsString("close")));
        }

        BoundedRegion enter = parseBounded(list.getConfig("enter"));

        Config dest = list.getConfig("destination");
        BoundedRegion destination = parseBounded(dest);
        float yaw = dest.getFloat("yaw", 0);
        float pitch = dest.getFloat("pitch", 0);

        parsedPortals.add(new Portal(type, open, close, enter, destination, yaw, pitch));
      } catch (Exception e) {
        Bukkit.getConsoleSender().sendMessage("Failed to parse portal: " + list.toString());
        e.printStackTrace();
      }
    }

    return parsedPortals;
  }

  public static List<Pad> getPads() {
    List<Pad> parsedPads = new ArrayList<>();

    for (Config list : pads) {
      try {
        BoundedRegion enter = parseBounded(list.getConfig("region"));
        Vector velocity = vecFromString(list.getAsString("velocity"));
        parsedPads.add(new Pad(enter, velocity));
      } catch (Exception e) {
        Bukkit.getConsoleSender().sendMessage("Failed to parse pad: " + list.toString());
        e.printStackTrace();
      }
    }

    return parsedPads;
  }

  public static List<Present> getPresents() {
    List<Present> parsedPresents = new ArrayList<>();

    for (Config list : presents) {
      try {
        Vector where = ConfigUtils.vecFromString(list.getAsString("location"));
        String slug = list.getAsString("slug");
        String family = list.getAsString("family");
        parsedPresents.add(new Present(where, slug, family));
      } catch (Exception e) {
        Bukkit.getConsoleSender().sendMessage("Failed to parse portal: " + list.toString());
        e.printStackTrace();
      }
    }

    return parsedPresents;
  }

  public static AtrioListener.Spawn getSpawn() {
    BoundedRegion region = parseBounded(spawn.getConfig("location"));
    float yaw = spawn.getFloat("yaw", 0);
    float pitch = spawn.getFloat("pitch", 0);
    Vector velocity = vecFromString(spawn.getAsString("velocity"));
    return new AtrioListener.Spawn(region, yaw, pitch, velocity);
  }

  @ConfigPath("inventory")
  public static class Inventory {

    @ConfigPath("server-menu")
    public static class ServerMenu {

      @Getter
      @ConfigKey
      private static boolean enabled;

      @Getter
      @ConfigKey
      private static int slot;
    }

    @ConfigPath("store")
    public static class Store {

      @Getter
      @ConfigKey
      private static boolean enabled;

      @Getter
      @ConfigKey
      private static int slot;
    }

    @ConfigPath("backpack")
    public static class Backpack {

      @Getter
      @ConfigKey
      private static boolean enabled;

      @Getter
      @ConfigKey
      private static int slot;
    }

    @ConfigPath("book")
    public static class Book {

      @Getter
      @ConfigKey
      private static boolean enabled;

      @Getter
      @ConfigKey
      private static int slot;

      @ConfigKey
      private static String name;
      @ConfigKey
      private static String author;
      @ConfigKey
      private static List<String> pages;

      public static String getName() {
        return Strings.addColors(name);
      }

      public static String getAuthor() {
        return Strings.addColors(author);
      }

      public static List<String> getPages() {
        return pages.stream().map((page) -> {
          page = page.replace("\\n", "\n");
          return Strings.addColors(page.replace("\\n", "\n"));
        }).collect(Collectors.toList());
      }
    }

    @ConfigPath("armor")
    public static class Armor {

      @Getter
      @ConfigKey
      private static String helmet;

      @Getter
      @ConfigKey
      private static String chestplate;

      @Getter
      @ConfigKey
      private static String leggings;

      @Getter
      @ConfigKey
      private static String boots;
    }
  }
}