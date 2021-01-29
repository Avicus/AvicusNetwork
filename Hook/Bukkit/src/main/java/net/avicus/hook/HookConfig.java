package net.avicus.hook;

import java.util.Optional;
import lombok.Getter;
import net.avicus.compendium.config.inject.ConfigKey;
import net.avicus.compendium.config.inject.ConfigPath;

public class HookConfig {

  @Getter
  @ConfigKey(key = "locales-path")
  public static String localesPath;

  @Getter
  @ConfigKey
  private static boolean backend;

  @ConfigKey(key = "shutdown-on-empty")
  @Getter
    /* Optional because by default the task should happen. */
  private static Optional<Boolean> shutdownOnEmpty;
  @Getter
  @ConfigKey(key = "crate-rewards")
  private static boolean rewardCrates;

  @ConfigPath("tracking")
  public static class Tracking {

    @Getter
    @ConfigKey
    private static boolean objectives;

    @Getter
    @ConfigKey
    private static boolean deaths;
  }

  @ConfigPath("achievements")
  public static class Achievements {

    @Getter
    @ConfigKey
    private static boolean enabled;
  }

  @ConfigPath("server")
  public static class Server {

    @Getter
    @ConfigKey
    private static Optional<String> name;

    @ConfigPath("session-logging")
    public static class SessionLogging {

      @Getter
      @ConfigKey
      private static boolean enabled;

      @Getter
      @ConfigKey
      private static int padding;
    }
  }

  @ConfigPath("chat")
  public static class Chat {

    @Getter
    @ConfigKey(key = "strip-color")
    private static boolean stripColor;
  }

  @ConfigPath("credits")
  public static class Credits {

    @Getter
    @ConfigKey(key = "initial-balance")
    private static int initialBalance;

    @ConfigPath("rewards")
    public static class Rewards {

      @Getter
      @ConfigKey
      private static boolean enabled;

      @Getter
      @ConfigPath("monument")
      @ConfigKey(key = "destroy")
      private static int monumentDestroy;

      @Getter
      @ConfigPath("monument")
      @ConfigKey(key = "damage")
      private static int monumentDamage;

      @Getter
      @ConfigPath("flag")
      @ConfigKey(key = "capture")
      private static int flagCapture;

      @Getter
      @ConfigPath("wool")
      @ConfigKey(key = "place")
      private static int woolPlace;

      @Getter
      @ConfigPath("wool")
      @ConfigKey(key = "pickup")
      private static int woolPickup;

      @Getter
      @ConfigPath("leakable")
      @ConfigKey(key = "leak")
      private static int leakableLeak;

      @Getter
      @ConfigPath("flag")
      @ConfigKey(key = "save")
      private static int flagSave;

      @Getter
      @ConfigPath("match")
      @ConfigKey(key = "kill-player")
      private static int killPlayer;

      @Getter
      @ConfigPath("match")
      @ConfigKey(key = "win-minimum")
      private static int winMinimum;

      @Getter
      @ConfigPath("match")
      @ConfigKey(key = "win-per-minute")
      private static int winPerMinute;

      @Getter
      @ConfigPath("match")
      @ConfigKey(key = "lose-minimum")
      private static int loseMinimum;

      @Getter
      @ConfigPath("match")
      @ConfigKey(key = "lose-per-minute")
      private static int losePerMinute;
    }
  }

  @ConfigPath("experience")
  public static class Experience {

    @Getter
    @ConfigKey(key = "initial-balance")
    private static int initialBalance;

    @ConfigPath("rewards")
    public static class Rewards {

      @Getter
      @ConfigKey
      private static boolean enabled;

      @Getter
      @ConfigPath("monument")
      @ConfigKey(key = "destroy")
      private static int monumentDestroy;

      @Getter
      @ConfigPath("monument")
      @ConfigKey(key = "damage")
      private static int monumentDamage;

      @Getter
      @ConfigPath("flag")
      @ConfigKey(key = "capture")
      private static int flagCapture;

      @Getter
      @ConfigPath("wool")
      @ConfigKey(key = "place")
      private static int woolPlace;

      @Getter
      @ConfigPath("wool")
      @ConfigKey(key = "pickup")
      private static int woolPickup;

      @Getter
      @ConfigPath("leakable")
      @ConfigKey(key = "leak")
      private static int leakableLeak;

      @Getter
      @ConfigPath("flag")
      @ConfigKey(key = "save")
      private static int flagSave;

      @Getter
      @ConfigPath("capture-point")
      @ConfigKey(key = "capture")
      private static int capturePointCapture;

      @Getter
      @ConfigPath("scorebox")
      @ConfigKey(key = "enter")
      private static int scoreBoxEnter;

      @Getter
      @ConfigPath("match")
      @ConfigKey(key = "kill-player")
      private static int killPlayer;

      @Getter
      @ConfigPath("match")
      @ConfigKey(key = "win-minimum")
      private static int winMinimum;

      @Getter
      @ConfigPath("match")
      @ConfigKey(key = "win-per-minute")
      private static int winPerMinute;

      @Getter
      @ConfigPath("match")
      @ConfigKey(key = "lose-minimum")
      private static int loseMinimum;

      @Getter
      @ConfigPath("match")
      @ConfigKey(key = "lose-per-minute")
      private static int losePerMinute;
    }
  }

  @ConfigPath("announcements")
  public static class Announcements {

    @Getter
    @ConfigKey
    private static boolean enabled;

    @Getter
    @ConfigKey
    private static int delay;
  }

  @ConfigPath("friends")
  public static class Friends {

    @Getter
    @ConfigKey
    private static boolean redis;
  }

  @ConfigPath("punishments")
  public static class Punishments {

    @Getter
    @ConfigKey
    private static boolean enabled;

    @Getter
    @ConfigKey
    private static boolean redis;
  }

  @ConfigPath("map-ratings")
  public static class MapRatings {

    @ConfigKey
    @Getter
    private static boolean enabled;

    @ConfigKey(key = "book-enabled")
    @Getter
    private static boolean bookEnabled;

    @ConfigKey(key = "message-delay")
    @Getter
    private static int delay;
  }

}
