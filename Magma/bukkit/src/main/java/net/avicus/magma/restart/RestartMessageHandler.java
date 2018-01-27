package net.avicus.magma.restart;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.avicus.magma.Magma;
import net.avicus.magma.redis.RedisMessage;
import net.avicus.magma.restart.RestartMessageHandler.RestartMessage;
import net.avicus.magma.util.AsyncRedisHandler;

public class RestartMessageHandler extends
    AsyncRedisHandler<RestartMessage> {

  public static RestartHandler RESTART_HANDLER = new BukkitRestartHandler();

  public RestartMessageHandler() {
    super(new String[]{"restart"});
  }

  @Override
  public void handle(RestartMessage message) {
    if (message.getGroupId() == Magma.get().localServer().getServerGroupId()) {
      RESTART_HANDLER.queue();
    }
  }

  @Override
  public RestartMessage readAsync(JsonObject json) {
    return new RestartMessage(json.get("group").getAsInt());
  }

  public static class RestartMessage implements RedisMessage {

    @Getter
    private final int groupId;

    public RestartMessage(int groupId) {
      this.groupId = groupId;
    }

    @Override
    public String channel() {
      return "restart";
    }

    @Override
    public JsonObject write() {
      JsonObject json = new JsonObject();
      json.addProperty("group", groupId);
      return json;
    }
  }
}
