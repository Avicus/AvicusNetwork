package net.avicus.hook;

import java.util.List;
import lombok.Getter;
import net.avicus.compendium.config.Config;
import net.avicus.compendium.config.inject.ConfigKey;
import net.avicus.compendium.config.inject.ConfigPath;

public class HookConfig {

  @ConfigPath("connection")
  public static class Connection {

    @Getter
    @ConfigKey
    private static String host;

    @Getter
    @ConfigKey
    private static int port;

    @Getter
    @ConfigKey
    private static String username;

    @Getter
    @ConfigKey
    private static String password;

    @Getter
    @ConfigKey(key = "server-id")
    private static int server;

    @Getter
    @ConfigKey
    private static String nickname;
  }

  @ConfigPath("groups")
  public static class Groups {

    @Getter
    @ConfigKey
    private static boolean enabled;
  }

  @ConfigPath("clients")
  public static class Clients {

    @Getter
    @ConfigKey
    private static boolean enabled;

    @Getter
    @ConfigKey(key = "unregistered-delay")
    private static int unregisteredDelay;
  }

  @ConfigPath("temp-channels")
  public static class TempChannels {

    @Getter
    @ConfigKey
    private static boolean enabled;

    @Getter
    @ConfigKey(key = "root-id")
    private static int rootId;

    @Getter
    @ConfigKey(key = "creation-help")
    private static List<String> creationHelp;

    @Getter
    @ConfigKey(key = "initial-success")
    private static String initialSuccess;

    @Getter
    @ConfigKey(key = "extension-success")
    private static String extensionSuccess;

    @ConfigPath("pricing")
    public static class Pricing {

      @Getter
      @ConfigKey
      private static List<Config> initial;

      @Getter
      @ConfigKey
      private static List<Config> extensions;
    }
  }

  @ConfigPath("messages")
  public static class Messages {

    @Getter
    @ConfigKey
    private static boolean enabled;

    @Getter
    @ConfigKey(key = "welcome")
    private static List<String> welcomeText;

    @Getter
    @ConfigKey(key = "register")
    private static List<String> registerText;

    @Getter
    @ConfigKey(key = "registered")
    private static List<String> registeredText;

    @Getter
    @ConfigKey(key = "register-kick")
    private static String registeredKick;
  }
}
