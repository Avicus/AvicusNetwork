package net.avicus.hook.backend;

import lombok.Getter;
import net.avicus.compendium.config.inject.ConfigKey;
import net.avicus.compendium.config.inject.ConfigPath;

public class BackendConfig {

  @Getter
  @ConfigKey(key = "gadget-conversion")
  private static boolean gadgetConversion;

  @ConfigPath("buycraft")
  public static class Buycraft {

    @Getter
    @ConfigKey
    private static boolean enabled;

    @Getter
    @ConfigKey(key = "api-key")
    private static String apiKey;

    @Getter
    @ConfigKey(key = "pool-size")
    private static int poolSize;

    @Getter
    @ConfigKey
    private static int period;
  }

  @ConfigPath("leaderboards")
  public static class Leaderboards {

    @Getter
    @ConfigKey
    private static boolean enabled;

    @Getter
    @ConfigKey(key = "xp")
    private static boolean xpEnabled;
  }
}
