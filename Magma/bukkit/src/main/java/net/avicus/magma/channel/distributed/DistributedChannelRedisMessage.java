package net.avicus.magma.channel.distributed;

import com.google.gson.JsonObject;
import java.util.Map;
import javax.annotation.Nullable;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.redis.RedisMessage;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class DistributedChannelRedisMessage implements RedisMessage {

  private final DistributedChannel channel;
  private final Server server;
  @Nullable
  private final User source;
  private final BaseComponent[] components;
  private final Map<String, String> context;

  DistributedChannelRedisMessage(DistributedChannel channel, Server server, @Nullable User source,
      BaseComponent[] components, Map<String, String> context) {
    this.channel = channel;
    this.server = server;
    this.source = source;
    this.components = components;
    this.context = context;
  }

  public DistributedChannel getChannel() {
    return this.channel;
  }

  public Server getServer() {
    return this.server;
  }

  @Nullable
  public User getSource() {
    return this.source;
  }

  public BaseComponent[] getComponents() {
    return this.components;
  }

  public Map<String, String> getContext() {
    return this.context;
  }

  @Override
  public String channel() {
    return DistributedChannelRedisMessageConsumer.ID;
  }

  @Override
  public JsonObject write() {
    JsonObject json = new JsonObject();
    json.addProperty("channel", this.channel.getId());
    json.addProperty("server", this.server.getId());
    if (this.source != null) {
      json.addProperty("source", this.source.getId());
    }
    json.addProperty("components", ComponentSerializer.toString(this.components));
    if (!this.context.isEmpty()) {
      JsonObject result = new JsonObject();
      for (Map.Entry<String, String> entry : this.context.entrySet()) {
        result.addProperty(entry.getKey(), entry.getValue());
      }
      json.add("context", result);
    }
    return json;
  }
}
