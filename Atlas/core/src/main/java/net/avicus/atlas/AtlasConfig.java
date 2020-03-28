package net.avicus.atlas;

import java.util.List;
import lombok.Getter;
import net.avicus.compendium.config.Config;
import net.avicus.compendium.config.inject.ConfigKey;
import net.avicus.compendium.config.inject.ConfigPath;

public class AtlasConfig {

  @Getter
  @ConfigKey
  private static List<Config> libraries;

  @Getter
  @ConfigKey(key = "max-group-imbalance")
  private static double maxGroupImbalance;

  @Getter
  @ConfigPath("rotation")
  @ConfigKey(key = "path")
  private static String rotationFile;

  @Getter
  @ConfigPath("rotation")
  @ConfigKey(key = "restart-on-end")
  private static boolean rotationRestart;

  @Getter
  @ConfigPath("rotation")
  @ConfigKey(key = "randomize")
  private static boolean rotationRandomize;

  @Getter
  @ConfigPath("rotation")
  @ConfigKey(key = "auto-start")
  private static boolean rotationAutoStart;

  @Getter
  @ConfigKey(key = "delete-matches")
  private static boolean deleteMatches;

  @Getter
  @ConfigKey(key = "scrimmage")
  private static boolean scrimmage;

  @Getter
  @ConfigPath("github")
  @ConfigKey(key = "auth")
  private static boolean githubAuth;

  @Getter
  @ConfigPath("github")
  @ConfigKey(key = "username")
  private static String githubUsername;

  @Getter
  @ConfigPath("github")
  @ConfigKey(key = "token")
  private static String githubToken;

  @ConfigPath("website")
  public static class Website {

    @ConfigKey(key = "base")
    @Getter
    private static String base;

    @ConfigKey(key = "map")
    @Getter
    private static String map;

    public static String resolvePath(String slug) {
      return base + map.replace("{0}", slug);
    }
  }

  @Getter
  @ConfigKey(key = "send-deprecation-warnings")
  private static boolean sendDeprecationWarnings;
}
