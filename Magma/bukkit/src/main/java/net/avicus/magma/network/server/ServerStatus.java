package net.avicus.magma.network.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.User;
import org.bukkit.ChatColor;
import org.bukkit.Skin;
import org.joda.time.Instant;

@Getter
@ToString
public class ServerStatus {

  private static final Skin SKIN_BLUE = new Skin(
      "eyJ0aW1lc3RhbXAiOjE0NjkzNzg1NDUzMTEsInByb2ZpbGVJZCI6ImY0NDFkODc5NjgzNjQyMzA5ZGE4Yzk0NzNiM2Q2NGI1IiwicHJvZmlsZU5hbWUiOiJBdmlzIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83MGVjNjY5NzU2NjlkNGQyODc3MjkzM2NjZDhkMmFlYTg0MjZlOTNlOTg1YmU2NjRlZDk3OGZiZTQ0MWE5ZWJkIn19fQ==",
      "dzebMHUrEk5jRV2X28Uc9OgXReeZGzRNL7qVFbM92LVZI8Ubr9pDr5+rUD8jancrmP2xFrcnIWmj1EatM+dQQdr2hMOrVB4JxHJgaeztpMsPkk+HoILAbnnAHQjy1DZLdHtC1ryae+S8dbicvx0O+AbiWRtKnfDPSVgDmDI7Hf1nBQmvGKrYvyzufR2R/3B9eShIHYmrBFSs+HsOu7Pe0whMPZggRxqFU8nh2ZFbNYPW5a6eqWE9vuAqXf2XYRF/agjOXIPL5Gv7vm6Ec2hdhQ4m/FpdpEwAnOJEyffQP7toEsC1vpIvnxAbkqNbhp1kyEYBp92iyMX74slPCnyNpuoGWkW1ZwuBI7aMXpXJvrSbWCXrfRYtp1yUQwzZ3TwCx6JWGuOlJzZ1nEefNV4WHqRP2/opYvZXmW33jWxiE4L2widciu2NoCe5pfIKspOsWheC7OaF9neyfnFOeO0LpukErMXOxhWamGiUgKUMzErgluPdaiM4b1mLHXf5a8X2M/hV/qQYDUN12foi2OdKKwBU0Qvo704Vpr+WWmm5wLxDGZBbCn5QBcUT/nAmgYygLBlHw6ZmGKsWd07Cj0tg+McfButWn7haEjGtFtGAuDUk/JU/jCn+ioq69C7QUCkWfM6/dRBEnBbPF4oyR3ETWCZ+NUPUMYcV5DYMjcOJ/Yw=");
  private static final Skin SKIN_RED = new Skin(
      "eyJ0aW1lc3RhbXAiOjE0NzM4OTU5ODEwNjUsInByb2ZpbGVJZCI6ImY0NDFkODc5NjgzNjQyMzA5ZGE4Yzk0NzNiM2Q2NGI1IiwicHJvZmlsZU5hbWUiOiJBdmlzIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iNGIyYjdmZjFmODBmMTMzYzlkOWJhNGI0NTgzODEzOThlZjI5Y2IyOGMxYmE0MjFkZTRmMWM2YjZhOGM5MCJ9fX0=",
      "Z3F3ccRc7FqbxwCEcjjAdS34b6436YsUyBqgLdqYd8ATsFflAqQd9VvVmUCZNF0YNDtoqx3sccS7kBct3Bpg9KqyHo5Mv6RdWNljbsjLf+pOqgBQFBwJsxgNiDrDT9fSkRdzEKyAwvPAbBM5mzrblCWIn8Ydumd5e5JClDhs0T54ZvG2tA1MMhHl+HYn/dYtCs3tgQkxTfelCnEv22wOd6Pq3Kr2PPi1CSKalId5E8uubS+xGNREBUqKpJUNE75AucbK/AZwqbDHAgkD8YUQoylxWnrV51G4ljbtZ7c0+sHpMMkXQRAwbWVUCpu/73jEU9oG1mF+feYm38u9y1Sog06Npqk8TohR2g+pZRloAtwP8FHQ9qFZzjf4vF7XHRqKboJ91APY/83nddUWsxNDtGYsii5EDXuhJrYmiwlLzE5ZeZc98J/XDBCggB7uCio+MLpHvMPheqICQ+TssA2hzIh3m1l8LXmRdvoyfkOZQjtX2SyhJn4YKiVRw94/5pTEWXZ0YWO1qA5mXt4vzDRYWtvZRbzke9eJWIyLcQWLoHtHSxDUP+c+eDFD8LycJS5kONh0AfbM4fpZmByoQYJv121+m2tM07b9TKOxC4iq8hbYYGGSj3Gd1ncgMlWYuV+S9DkHbP3UcI8k5kA5tldrQxx5T8ZpAbZO0Itd/Rdphow=");
  private static final Skin SKIN_GREEN = new Skin(
      "eyJ0aW1lc3RhbXAiOjE0NjkzNzg4OTQ0MTIsInByb2ZpbGVJZCI6ImY0NDFkODc5NjgzNjQyMzA5ZGE4Yzk0NzNiM2Q2NGI1IiwicHJvZmlsZU5hbWUiOiJBdmlzIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zMmRlZmE5MTkzYmYyNDdlNDMzMDMyOWRhYTI0NGYxOTFiOTZmODk4ZGU1YjEzYmJjY2ExM2U4ZjYyNTc0OSJ9fX0=",
      "kfqQhpUZbP9Ff7vkRFHWP+e4a8qx/x18lOQuleEeYBL5YB1vtB4ctv9MTIzvmrcp88FQsaEDWcmOFrHx9x4bRbbMRqJ2I02vkgorOoTG2TbnW6NzDMkFUsVKK6Z6z1Jv5t6ItfVz7V+lrrQITM2cpTXHOtUPVviblJjfgg7bu1N00MsWwIM9EmsO7sW5DpNVbs1G5RS4KBrIj/then9H71yWOZ5TkeGbK1Ivq/akQC1qoNKYlts+GqRtm0lhuKGP/bx9w4iHOK29JRGnXk6jWOhfuf8DDj71R74M4/3/hCV8NHsXm24rwEJfQdTVDo8m0seP1n/v6xqef4jq5N+v1C5hJAPlGsIqfOV20xBdNZ8BCoruaDn5VscTkKc8sRrZeBGMjbOIdpL/Fzu5Wx1ZHs31pvV98uxo4fwlg+mTrKSNtr8rnAyPBGkYSnxEv3dqJFNrTAJGvKRo+o20eBaKBdY7/4jFEtlB0kJVl9b6TZR5HeiJBdub8/PAqElZb6DVjz6FghkLCXYv2qp9viEItnytYIJCYRpafkZNxHYpT/anJ6XebSPLWh4Yn4QbCAecenjRGTyE76UuEdu2cJG+HdHVK0xuIQH6HLv3jo6FQJoRl0gY68gZ2FBFi20rcbILzQdh3P8Ib3S82osxcXyPibag5BHugNiTizhmOZC91gw=");
  private static final Skin SKIN_GOLD = new Skin(
      "eyJ0aW1lc3RhbXAiOjE0NjkzODQ0OTM5ODgsInByb2ZpbGVJZCI6ImY0NDFkODc5NjgzNjQyMzA5ZGE4Yzk0NzNiM2Q2NGI1IiwicHJvZmlsZU5hbWUiOiJBdmlzIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84M2ViNGNkMmM1YWZhYzIxODhlODU1Mjk1ZGU4MTQ2YjgxNGY5YTc3MDU5MmVjZWM0NDUzYjdiZThiNDljIn19fQ==",
      "B0lsID7tUSxfisTeziMczCWb7pN982b/9nuwmiBs8ov/dKfGH32O4HJ4Ne9LEIZX9/XuuMyYCFwdY3F6Auw6Z+C1S3xI+DGFe7EXT4grof0qQS6mIhMT4S5Er4m63cW+YWdXn1vKM8HGu9SNTwFZkxpiVzoE+HQuuwt1PdTA0XOq+/erVPAgnTxQrVFMFRn7N7MfpVEDz3WCwvTnN4dU0rL3qrptqCXpSlqvEvwi7Md++WF875ghy5r8DKUk5+ZxRgLz9XwR7z5Gt4ak4DgOevokgvLol4IK68G1LnfHcirQ5l8BEJ1ZvomHwcxMP7QdIpGVhDzlNr+UIalqvSiQjRhlHKntyHG/r565E0w0ybymfStcFakcuqU5MlAQdoizzZ1sxdB1ZD+zrMPy6WlibUyXY+zilKq6s7c+ZbZte0MKy6bWOZS+wStGuhSOv1ttfa80XnuYNJzc9735+X6QB/tCVFjA2EzqZyuwTlD/cWUaPTX14yo+dDAoOwyXaPZe7VsWxTIvVbEVeSOm64AyI0305mLyojol5jy6qJWfrvGRBaNsHo2qY9kCi+cL99IGdl2nGZNIkOvlIebjH3W8uty4vHlNhi+uzUI225N64SLEMKhdQNFHycFH2lzRrEZAJz4v8dDtFceNc/wPjE5ndIi+IGkIXRKm0sNjT5gtGmU=");
  private static final Skin SKIN_AQUA = new Skin(
      "eyJ0aW1lc3RhbXAiOjE0NjkzODQ3MTI2OTEsInByb2ZpbGVJZCI6ImY0NDFkODc5NjgzNjQyMzA5ZGE4Yzk0NzNiM2Q2NGI1IiwicHJvZmlsZU5hbWUiOiJBdmlzIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jMjFlNDYxMTI1NjJiNzFiZTY2MzgwZjgyMjUyNGNhZTRiNGEzODliMWJkNzQ0MmZkYzQ5NmE4ZWIzMWQ1MTU5In19fQ==",
      "pcXtmS2Db/5JbMN9XHO3LO9e3/NmRou9eLKagGmDGZ5cbrtwCyJMrvMlg6VX/Vh6ftsuccNO07gGWASyiF36z6m4MM9bIVcvNyIQivmpZ3uFhF3V7REoNODIwiYndoDDYGlyGoDctkbaZbM3DBex9xGNZmsjRw+NuxC7Rv5d/EW5p161ez08OGthOTBz7gHh+6RFtkfzvqBsa0N/KZSjVSJ/pJ6HsoEDrGnZlxjeh75e1BVFV3Hq4wu8LOUOKqJLMNatKQvAl87T1yQ7WYp6oMqBZRHw8K+04SSS3fwLSqMiNuP0Re35YXgZqLIJHEvBg7uzpRZIbF/mFdxZx61Yt0e0K69yrWCneAHED7Vx3vuWL++iemWoFPt/NRvXJo3H8XB/7lXZFVYVvoDuDllC4OG0cySEzO3Z/+BXzETmcqW1wACuTxlku5mgvqb742lX2Iln65BRyCEvPHABN3srPySxXCBKLmk8VDIvtHCucK/LZI4w8dsmSw6jqI+eVXrJTnZn+vnR70tVQt3qmSEoupN2HXXf7NRpXR58RnqYh9npG/An5JXI8t67Q+z+tEOQ2OZWGSOwii8NJjZo/eUaBYUsOUwawo8w1vIiloAPmWdNWJLVRdN6MJMHhELf6FWTmPSLLzrV6SkAOzRdog/IXzdx7Xe8xWs/krkRIQqnNJU=");
  private static final Skin SKIN_GRAY = new Skin(
      "eyJ0aW1lc3RhbXAiOjE0NjkzODYwMjM5OTQsInByb2ZpbGVJZCI6ImY0NDFkODc5NjgzNjQyMzA5ZGE4Yzk0NzNiM2Q2NGI1IiwicHJvZmlsZU5hbWUiOiJBdmlzIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82MzM5NTEzMDM2ZDYyNmFjY2VlNTRiMmVkNGY2YjY5MDEzNjZkY2FlYzI1MWI0NzcxNWU1NjdlYjRmODU1In19fQ==",
      "UYDzxY2WqxoudqcllHccrWPOqM+9Aj3mOdkxZvR8UpyzVuff668sUJEzAntxQ+pFS9QMH40OOHzd8nzQKl38l0IoOMVc2oIwfYwphgHBHQNQrSUSASum3aLRbXeScSlhrVoba/6x9wfQq3cTkwdFrprvA1qnztzIGIItSXuxYMGmCWfzKP0wM1h1SlVNx1dRZLARpH0n7K3+YRPXlEVQdhckNHOFPcqCobZ9bEuUdVRcczoamqlf6Z2mbajLtxkgihUEnNIW+7Fh9f+AoX+GD6gxvFrYosCCIp99ieQv/eTDbptODZTki0bYxD1paQQRKej5nGQDElCEA3kFiQy/e+X/23HLNWekexjyB3qdQAjrpJLSH1aU0ahMskETbiNvkbaHIfGIRwHhaePHnrtfvUHyz+2CUM6tII1lRIcIefT1+znz2XTaQluXdNxcMjprbE1dCRUbJZgFn5GS+6NWW7aPoLX59VW2m6h+o2gsf+9fYP+jwdpMmshVytfVBGnPOrY6p3kHkfG8lppHlXQWwmGxZwoU03pj8Nz7GhGgoOHqOh+khMFsGK68G+izX2FiA2Mfg9sNXwyKyCH9FtaSokefLS9WrqM/KU7M00iZ+Opy3y1qCuEYoQMf1DkGPWpKjoNc/xqtik11sJ2ofBcnqw5hTDdE1BWPvC0LH+N2OsQ=");
  private final int id;
  private final Server server;
  private final boolean online;
  private final Instant timestamp;
  private final List<User> players;  // all players connected
  private final int playingCount;  // number of players in game count
  private final int spectatingCount;  // number of players spectating
  private final int maxPlayers;  // the max player count in game
  private final State state;
  private final String message;
  private final JsonObject custom;

  /**
   * Default offline server status.
   */
  public ServerStatus(Server server) {
    this(server, false, Instant.now(), new ArrayList<>(), 0, 0, 0, State.OFFLINE, null,
        new JsonObject());
  }

  public ServerStatus(Server server,
      boolean online,
      Instant timestamp,
      List<User> players,
      int playingCount,
      int spectatingCount,
      int maxPlayers,
      State state,
      String message,
      JsonObject custom) {
    this.id = server.getId();
    this.server = server;
    this.online = online;
    this.timestamp = timestamp;
    this.players = players;
    this.playingCount = playingCount;
    this.spectatingCount = spectatingCount;
    this.maxPlayers = maxPlayers;
    this.state = state;
    this.message = message;
    this.custom = custom;
  }

  public static ServerStatus deserialize(Database mysql, Server server, JsonObject json) {
    boolean online = json.get("online").getAsBoolean();

    if (!online) {
      return new ServerStatus(server);
    }

    Instant timestamp = new Instant(json.get("timestamp").getAsLong());
    List<User> players = new ArrayList<>();
    int maxPlayers = json.get("max-players").getAsInt();
    State state = State.valueOfOrDefault(json.get("state").getAsString());
    String message = json.get("message").isJsonNull() ? null : json.get("message").getAsString();
    JsonObject custom = json.get("custom").getAsJsonObject();

    JsonArray jsonPlayers = json.get("players").getAsJsonArray();
    for (int i = 0; i < jsonPlayers.size(); i++) {
      int userId = jsonPlayers.get(i).getAsInt();
      players.add(mysql.getUsers().findById(userId).get());
    }
    int playerCount =
        json.has("player-count") ? json.get("player-count").getAsInt() : jsonPlayers.size();
    int spectatingCount =
        json.has("spectating-count") ? json.get("spectating-count").getAsInt() : 0;

    return new ServerStatus(server, true, timestamp, players, playerCount, spectatingCount,
        maxPlayers, state, message, custom);
  }

  public Optional<String> getMessage() {
    if (this.message == null || this.message.length() == 0) {
      return Optional.empty();
    }
    return Optional.of(ChatColor.translateAlternateColorCodes('&', this.message));
  }

  public JsonObject serialize() {
    JsonArray jsonPlayers = new JsonArray();

    if (this.players != null) {
      for (User user : this.players) {
        jsonPlayers.add(new JsonPrimitive(user.getId()));
      }
    }

    JsonObject json = new JsonObject();
    json.addProperty("server", this.server.getId());
    json.addProperty("online", this.online);
    json.addProperty("timestamp", this.timestamp.getMillis());
    json.add("players", jsonPlayers);
    json.addProperty("player-count", this.playingCount);
    json.addProperty("spectating-count", this.spectatingCount);
    json.addProperty("max-players", this.maxPlayers);
    json.addProperty("message", this.message);
    if (this.state != null) {
      json.addProperty("state", this.state.name());
    }
    json.add("custom", this.custom == null ? new JsonObject() : this.custom);

    return json;
  }

  @Getter
  public enum State {
    DEFAULT(ChatColor.GOLD, SKIN_GOLD),
    STARTING(ChatColor.GREEN, SKIN_GREEN),
    PLAYING(ChatColor.DARK_AQUA, SKIN_BLUE),
    CYCLING(ChatColor.AQUA, SKIN_AQUA),
    OFFLINE(ChatColor.GRAY, SKIN_RED);

    private final ChatColor color;
    private final Skin skin;

    State(ChatColor color, Skin skin) {
      this.color = color;
      this.skin = skin;
    }

    public static State valueOfOrDefault(String state) {
      if (state == null) {
        return DEFAULT;
      }
      try {
        return valueOf(state);
      } catch (Exception e) {
        return DEFAULT;
      }
    }
  }
}