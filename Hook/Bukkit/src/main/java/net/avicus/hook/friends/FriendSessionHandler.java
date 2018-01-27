package net.avicus.hook.friends;

import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.UnlocalizedComponent;
import net.avicus.compendium.sound.SoundEvent;
import net.avicus.compendium.sound.SoundLocation;
import net.avicus.compendium.sound.SoundType;
import net.avicus.hook.Hook;
import net.avicus.hook.friends.FriendSessionHandler.FriendSessionMessage;
import net.avicus.hook.utils.Events;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.ServerCategory;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.rtp.RTPHelpers;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.redis.RedisMessage;
import net.avicus.magma.util.AsyncRedisHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FriendSessionHandler extends AsyncRedisHandler<FriendSessionMessage> {

  private HashMap<Integer, FriendJoinMessage> recentLogins = new HashMap<>();
  private HashMap<Integer, FriendLeaveMessage> recentLogouts = new HashMap<>();

  public FriendSessionHandler() {
    super(new String[]{"friend-join", "friend-leave"});
    HookTask.of(() -> {
      HashMap<Integer, FriendJoinMessage> loginCopy = new HashMap<>();
      HashMap<Integer, FriendLeaveMessage> logoutCopy = new HashMap<>();

      loginCopy.putAll(recentLogins);
      logoutCopy.putAll(recentLogouts);

      recentLogouts.clear();
      recentLogins.clear();

      for (Map.Entry<Integer, FriendLeaveMessage> entry : logoutCopy.entrySet()) {
        if (loginCopy.containsKey(entry.getKey())) {
          broadcast(loginCopy.get(entry.getKey()), Optional.of(entry.getValue()));
          loginCopy.remove(entry.getKey());
        } else {
          broadcast(entry.getValue(), Optional.empty());
        }
      }

      for (Map.Entry<Integer, FriendJoinMessage> entry : loginCopy.entrySet()) {
        broadcast(entry.getValue(), Optional.empty());
      }
    }).repeat(20, 60);
  }

  @Override
  public void handle(FriendSessionMessage message) {
    if (message instanceof FriendJoinMessage) {
      recentLogins.put(message.getUser().getId(), (FriendJoinMessage) message);
    } else {
      recentLogouts.put(message.getUser().getId(), (FriendLeaveMessage) message);
    }
  }

  private void broadcast(FriendSessionMessage message, Optional<FriendSessionMessage> previous) {
    SoundLocation location = SoundLocation.FRIEND_JOIN;
    LocalizableFormat format = Messages.GENERIC_JOINED_SERVER;
    if (message instanceof FriendLeaveMessage) {
      format = Messages.GENERIC_LEFT_SERVER;
      location = SoundLocation.FRIEND_LEAVE;
    }

    if (previous.isPresent()) {
      format = Messages.GENERIC_SWITCHED_SERVERS;
    }

    for (Player player : Bukkit.getOnlinePlayers()) {
      User user = Users.user(player);

      Localizable friendName = new UnlocalizedComponent(RTPHelpers
          .permissibleClickablePlayer(player, message.getServer(), message.getUser(),
              player.getLocale(), true));
      Localizable oldServerName = previous.isPresent() ? new UnlocalizedComponent(
          RTPHelpers.clickableServer(previous.get().getServer(), player.getLocale())) : null;
      Localizable newServerName = new UnlocalizedComponent(
          RTPHelpers.clickableServer(message.getServer(), player.getLocale()));

      if (Friends.isFriend(user, message.getUser().getId())) {
        if (oldServerName != null) {
          player.sendMessage(
              format.with(ChatColor.GRAY, friendName, oldServerName, newServerName));
        } else {
          player.sendMessage(format.with(ChatColor.GRAY, friendName, newServerName));
        }

        SoundEvent call = Events.call(new SoundEvent(player, SoundType.NONE, location));
        call.getSound().play(player, 1F);
      }
    }
  }

  @Override
  public FriendSessionMessage readAsync(JsonObject json) {
    int serverId = json.get("server_id").getAsInt();
    Server server = Hook.database().getServers().findById(serverId).orElse(null);
    if (server == null) {
      return null;
    }

    Optional<ServerCategory> local = Magma.get().localServer()
        .getCategory(Magma.get().database().getServerCategories());
    Optional<ServerCategory> serverCategory = Magma.get().database().getServerCategories()
        .fromServer(server);

    if (local.isPresent()) {
      if (local.get().getOptions().isExternalFriends() || local.get().getId() == serverCategory
          .get().getId()) {
        return null;
      }
    }
    int userId = json.get("user_id").getAsInt();
    User user = Hook.database().getUsers().findById(userId).get();

    String type = json.get("type").getAsString();
    if (type.equals("join")) {
      return new FriendJoinMessage(user, server);
    } else {
      return new FriendLeaveMessage(user, server);
    }
  }

  public static class FriendJoinMessage extends FriendSessionMessage {

    public FriendJoinMessage(User user, Server server) {
      super(user, server);
    }

    @Override
    public String channel() {
      return "friend-join";
    }

    @Override
    public JsonObject write() {
      JsonObject base = super.write();
      base.addProperty("type", "join");
      return base;
    }
  }

  public static class FriendLeaveMessage extends FriendSessionMessage {

    public FriendLeaveMessage(User user, Server server) {
      super(user, server);
    }

    @Override
    public String channel() {
      return "friend-leave";
    }

    @Override
    public JsonObject write() {
      JsonObject base = super.write();
      base.addProperty("type", "leave");
      return base;
    }
  }

  public static abstract class FriendSessionMessage implements RedisMessage {

    @Getter
    private final User user;
    @Getter
    private final Server server;

    public FriendSessionMessage(User user, Server server) {
      this.user = user;
      this.server = server;
    }

    @Override
    public JsonObject write() {
      JsonObject json = new JsonObject();
      json.addProperty("server_id", server.getId());
      json.addProperty("user_id", this.user.getId());
      return json;
    }
  }
}
