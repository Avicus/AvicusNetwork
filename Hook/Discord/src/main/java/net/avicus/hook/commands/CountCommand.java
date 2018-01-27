package net.avicus.hook.commands;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.util.List;
import java.util.Map;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.redis.Redis;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class CountCommand implements DiscordCommand {

  private static final JsonParser parser = new JsonParser();

  @Override
  public void execute(CommandContext context, List<String> args) throws Exception {
    Redis redis = context.getHook().getRedis();
    Map<String, String> map = redis.hgetall("servers");
    List<ServerStatus> statuses = Lists.newArrayList();
    for (String id : map.keySet()) {
      try {
        Server server = context.getHook().getDatabase().getServers().findById(Integer.parseInt(id))
            .orElse(null);

        if (server == null) {
          continue;
        }

        String raw = map.get(id);
        JsonObject json = parser.parse(raw).getAsJsonObject();
        statuses.add(ServerStatus.deserialize(context.getHook().getDatabase(), server, json));

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (statuses.isEmpty()) {
      context.getLocation().sendMessage(context.getHook()
          .generateRichMessage("Oh no :(", "No servers are online right now, maybe contact a dev.",
              Color.RED)).complete();
      return;
    }

    List<MessageEmbed.Field> servers = Lists.newArrayList();

    statuses.forEach(s -> servers.add(s.generateDisplay()));

    MessageEmbed response = context.getHook().generateRichMessage("Player Counts",
        "Here is a list of the latest player counts by server", Color.magenta, servers);
    context.getLocation().sendMessage(response).complete();
  }

  static class ServerStatus {

    private final Server server;
    private final boolean online;
    private final int playing;
    private final int total;
    private final String mapName;

    public ServerStatus(Server server) {
      this(server, false, 0, 0, "");
    }

    public ServerStatus(Server server, boolean online, int playing, int total, String mapName) {
      this.server = server;
      this.online = online;
      this.playing = playing;
      this.total = total;
      this.mapName = mapName;
    }

    public static ServerStatus deserialize(Database mysql, Server server, JsonObject json) {
      boolean online = json.get("online").getAsBoolean();

      if (!online) {
        return new ServerStatus(server);
      }

      int maxPlayers = json.get("max-players").getAsInt();
      String message = json.get("message").isJsonNull() ? null : json.get("message").getAsString();

      JsonArray jsonPlayers = json.get("players").getAsJsonArray();
      int playerCount =
          json.has("player-count") ? json.get("player-count").getAsInt() : jsonPlayers.size();

      return new ServerStatus(server, true, playerCount, maxPlayers, message);
    }

    public MessageEmbed.Field generateDisplay() {
      if (!this.online) {
        return new MessageEmbed.Field(this.server.getName(), "OFFLINE", true);
      }

      if (this.playing == total || this.mapName == null) {
        return new MessageEmbed.Field(this.server.getName(), this.total + " players online.",
            true);
      }

      return new MessageEmbed.Field(this.server.getName(),
          this.playing + "/" + this.total + " online playing " + this.mapName, true);
    }
  }
}
