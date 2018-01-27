package net.avicus.hook;


import java.util.logging.Logger;
import lombok.Getter;
import net.avicus.compendium.config.ConfigFile;
import net.avicus.magma.redis.Redis;

public class Hook {

  @Getter
  private final Logger log;
  @Getter
  private final Redis redis;


  public Hook(ConfigFile dbConfig) {
    this.log = Main.getLogger("ServerManager");

    dbConfig.injector(DbConfig.class).inject();

    // Redis
    redis = DbConfig.RedisConfig.create().build();
    try {
      redis.enable();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }

    redis.register(new ServerActionHandler());
  }

  public void start() {
    this.log.info("Sever Manager starting up!");

    this.log.info("Configuration loaded");
  }
}
