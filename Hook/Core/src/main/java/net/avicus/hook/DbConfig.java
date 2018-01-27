package net.avicus.hook;

import java.util.Optional;
import lombok.Getter;
import net.avicus.compendium.config.inject.ConfigKey;
import net.avicus.compendium.config.inject.ConfigPath;
import net.avicus.magma.redis.Redis;
import net.avicus.quest.database.DatabaseConfig;
import net.avicus.quest.database.DatabaseConfig.Builder;

public class DbConfig {

  @ConfigPath("mysql")
  public static class MySQLConfig {

    @ConfigKey
    private static String host;

    @ConfigKey
    private static String username;

    @ConfigKey
    private static String password;

    @ConfigKey
    private static String database;

    public static DatabaseConfig create() {
      Builder builder = DatabaseConfig.builder(host, database, username, password);
      builder.reconnect(true);
      return builder.build();
    }
  }

  @ConfigPath("redis")
  public static class RedisConfig {

    @ConfigKey
    private static String host;

    @ConfigKey
    private static int database;

    @ConfigPath("auth")
    @ConfigKey
    private static Optional<Boolean> enabled;

    @Getter
    @ConfigPath("auth")
    @ConfigKey
    private static Optional<String> password;

    public static Redis.Builder create() {
      Redis.Builder builder = Redis.builder(host);
      builder.database(database);
      if (enabled.isPresent() && enabled.get() && password.isPresent()) {
        builder.password(password.get());
      }
      return builder;
    }
  }
}
