package net.avicus.magma.network.rtp;

import com.google.gson.JsonObject;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.redis.RedisMessage;

public class RemoteTeleportRedisMessage implements RedisMessage {

  @Getter(AccessLevel.PACKAGE)
  private final Server server;
  @Getter(AccessLevel.PACKAGE)
  private final UUID victim;
  @Getter(AccessLevel.PACKAGE)
  private final UUID target;

  RemoteTeleportRedisMessage(Server server, UUID victim, UUID target) {
    this.server = server;
    this.victim = victim;
    this.target = target;
  }

  @Override
  public String channel() {
    return RemoteTeleportRedisMessageConsumer.CHANNEL_ID;
  }

  @Override
  public JsonObject write() {
    JsonObject json = new JsonObject();
    json.addProperty("server", this.server.getId());
    json.addProperty("victim", this.victim.toString());
    json.addProperty("target", this.target.toString());
    return json;
  }
}
