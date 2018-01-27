package net.avicus.hook;

import net.avicus.compendium.locale.LocaleBundle;
import net.avicus.magma.Magma;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.redis.Redis;
import org.bukkit.Bukkit;

public class Hook {

  private static Boolean hasAtlas;
  private static Boolean hasLib;
  private static Boolean hasAtrio;

  public static HookPlugin plugin() {
    return HookPlugin.getInstance();
  }

  public static LocaleBundle locales() {
    return HookPlugin.getInstance().getLocaleBundle();
  }

  public static Database database() {
    return Magma.get().database();
  }

  public static Redis redis() {
    return Magma.get().getRedis();
  }

  public static Server server() {
    return Magma.get().localServer();
  }

  /**
   * Check if Atlas is enabled on this server.
   */
  public static boolean atlas() {
    if (hasAtlas == null) {
      hasAtlas = Bukkit.getServer().getPluginManager().getPlugin("Atlas") != null;
    }

    return hasAtlas;
  }

  /**
   * Check if Libs is enabled on this server.
   */
  public static boolean disguises() {
    if (hasLib == null) {
      hasLib = Bukkit.getServer().getPluginManager().getPlugin("LibsDisguises") != null;
    }

    return hasLib;
  }

  /**
   * Check if Atrio is enabled on this server.
   */
  public static boolean lobby() {
    if (hasAtrio == null) {
      hasAtrio = Bukkit.getServer().getPluginManager().getPlugin("Atrio") != null;
    }

    return hasAtrio;
  }
}
