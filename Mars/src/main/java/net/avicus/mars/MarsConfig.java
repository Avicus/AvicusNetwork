package net.avicus.mars;

import lombok.Getter;
import net.avicus.compendium.config.inject.ConfigKey;
import net.avicus.compendium.config.inject.ConfigPath;

public class MarsConfig {

  @Getter
  @ConfigKey(key = "scrimmage")
  private static boolean scrimmageEnabled;

  @ConfigPath("tournament")
  public static class Tournament {

    @Getter
    @ConfigKey(key = "id")
    private static int id;

    @Getter
    @ConfigKey(key = "enabled")
    private static boolean enabled;
  }
}
