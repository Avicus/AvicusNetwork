package net.avicus.magma.network.server.qp;

import com.google.gson.JsonObject;
import java.util.Date;
import lombok.Data;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.network.server.Servers;
import net.avicus.magma.network.server.qp.PlayerRequestHandler.PlayerRequestMessage;
import net.avicus.magma.redis.RedisMessage;
import net.avicus.magma.util.AsyncRedisHandler;

public class PlayerRequestHandler extends AsyncRedisHandler<PlayerRequestMessage> {

  protected PlayerRequestHandler() {
    super(new String[]{"player-request"});
  }

  @Override
  public PlayerRequestMessage readAsync(JsonObject json) {
    int serverId = json.get("server_id").getAsInt();
    Server server = Servers.getCachedServer(serverId).orElse(null);
    int players = json.get("players_needed").getAsInt();
    int slots = json.get("slots_available").getAsInt();
    Date expiration = new Date(json.get("expiration").getAsLong());

    return new PlayerRequestMessage(server, players, slots, expiration);
  }

  @Override
  public void handle(PlayerRequestMessage message) {
    QuickPlay.onRequest(message);
  }

  @Data
  public static class PlayerRequestMessage implements RedisMessage {

    private final Server server;
    private final int playersNeeded;
    private final int slotsAvailable;
    private final Date expiration;

    @Override
    public String channel() {
      return "player-request";
    }

    public int priority() {
      if (this.playersNeeded > 0) {
        return 25 * (int) Math.floor(1 / (double) this.playersNeeded);
      } else if (this.slotsAvailable > 0) {
        return 10 * this.slotsAvailable;
      } else {
        return 0;
      }
    }

    @Override
    public JsonObject write() {
      JsonObject json = new JsonObject();
      json.addProperty("server_id", this.server.getId());
      json.addProperty("players_needed", this.playersNeeded);
      json.addProperty("slots_available", this.slotsAvailable);
      json.addProperty("expiration", this.expiration.getTime());
      return json;
    }
  }
}
