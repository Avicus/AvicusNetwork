package net.avicus.hook;

import java.util.List;
import lombok.Getter;
import net.avicus.compendium.config.Config;
import net.avicus.compendium.config.inject.ConfigKey;

public class HookConfig {

  @Getter
  @ConfigKey
  private static String guild;

  @Getter
  @ConfigKey(key = "tm-guild")
  private static String tmGuild;

  @Getter
  @ConfigKey
  private static int tournament;

  @Getter
  @ConfigKey
  private static String token;

  @Getter
  @ConfigKey
  private static String status;

  @Getter
  @ConfigKey
  private static boolean debug;

  @Getter
  @ConfigKey
  private static boolean commands;

  @Getter
  @ConfigKey(key = "prestige-poll")
  private static String prestigePoll;

  @Getter
  @ConfigKey(key = "forum-polls")
  private static List<Config> fourmPolls;
}
