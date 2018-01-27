package net.avicus.magma.network.rtp;

import com.google.gson.JsonObject;
import java.util.UUID;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.util.AsyncRedisHandler;

public class RemoteTeleportRedisMessageConsumer extends
    AsyncRedisHandler<RemoteTeleportRedisMessage> {

  static final String CHANNEL_ID = "remote-teleport";
  private final RemoteTeleports processor;

  RemoteTeleportRedisMessageConsumer(RemoteTeleports processor) {
    super(new String[]{CHANNEL_ID});
    this.processor = processor;
  }

  @Override
  public RemoteTeleportRedisMessage readAsync(JsonObject json) {
    final Server server = Magma.get().database().getServers()
        .findById(json.get("server").getAsInt()).orElseThrow(() -> new IllegalArgumentException(
            "Could not find Server " + json.get("server").getAsInt()));
    final UUID victim = UUID.fromString(json.get("victim").getAsString());
    final UUID target = UUID.fromString(json.get("target").getAsString());
    return new RemoteTeleportRedisMessage(server, victim, target);
  }

  @Override
  public void handle(RemoteTeleportRedisMessage message) {
    this.processor.queue(message.getServer(), message.getVictim(), message.getTarget());
  }
}
