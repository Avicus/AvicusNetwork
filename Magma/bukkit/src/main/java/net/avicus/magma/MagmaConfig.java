package net.avicus.magma;

import java.util.List;
import lombok.Getter;
import net.avicus.compendium.config.Config;
import net.avicus.compendium.config.inject.ConfigKey;
import net.avicus.compendium.config.inject.ConfigPath;
import org.joda.time.Duration;

public final class MagmaConfig {

  @Getter
  @ConfigKey(key = "locales-path")
  public static String localesPath;

  @ConfigPath("channels")
  public static final class Channel {

    @Getter
    @ConfigKey
    public static boolean enabled;

    @ConfigPath("staff")
    public static final class Staff {

      @Getter
      @ConfigKey
      public static boolean enabled;
    }

    @ConfigPath("reports")
    public static final class Report {

      @ConfigKey
      private static long cooldown;
      @Getter
      @ConfigKey
      private static boolean enabled;

      public static Duration getCooldown() {
        return Duration.standardSeconds(cooldown);
      }
    }
  }

  @ConfigPath("api")
  public static final class API {

    @Getter
    @ConfigKey
    private static String url;

    @Getter
    @ConfigKey
    private static String key;

    @Getter
    @ConfigKey
    private static boolean mock;
  }

  @ConfigPath("alerts")
  public static class Alerts {

    @Getter
    @ConfigKey
    private static boolean enabled;

    @Getter
    @ConfigKey
    private static int poll;
  }

  @ConfigPath("freeze")
  public static final class Freeze {

    @Getter
    @ConfigKey
    private static boolean enabled;

    @ConfigPath("radius")
    public static final class Radius {

      @Getter
      @ConfigKey
      private static int extinguish;

      @Getter
      @ConfigKey
      private static int tnt;
    }
  }

  @ConfigPath("server")
  public static final class Server {

    @ConfigPath("gui")
    public static final class Gui {

      @Getter
      @ConfigKey
      private static int rows;

      @Getter
      @ConfigKey
      private static List<Config> slots;
    }

    @ConfigPath("quick-play")
    public static final class QuickPlay {

      @Getter
      @ConfigKey
      private static boolean enabled;
    }
  }
}
