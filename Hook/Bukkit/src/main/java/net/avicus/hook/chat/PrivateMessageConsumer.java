package net.avicus.hook.chat;

import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Data;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.compendium.settings.Setting;
import net.avicus.compendium.settings.types.SettingTypes;
import net.avicus.compendium.sound.SoundEvent;
import net.avicus.compendium.sound.SoundLocation;
import net.avicus.compendium.sound.SoundType;
import net.avicus.hook.Hook;
import net.avicus.hook.chat.PrivateMessageConsumer.PrivateMessage;
import net.avicus.hook.friends.Friends;
import net.avicus.hook.utils.Events;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.ServerCategory;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.redis.RedisMessage;
import net.avicus.magma.util.AsyncRedisHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrivateMessageConsumer extends AsyncRedisHandler<PrivateMessage> {

  final static Setting<PrivateMessageScope> PRIVATE_MESSAGES_SETTING = new Setting<>(
      "private-messages",
      SettingTypes.enumOf(PrivateMessageScope.class),
      PrivateMessageScope.ALL,
      Messages.PRIVATE_MESSAGES,
      Messages.PRIVATE_MESSAGES_SUMMARY
  );
  private final static Map<UUID, User> lastMessages = new HashMap<>();

  protected PrivateMessageConsumer() {
    super(new String[]{"private-msg"});
  }

  private static boolean shouldDeliver(User from, User to) {
    PrivateMessageScope option = PlayerSettings.get(to.getUniqueId(), PRIVATE_MESSAGES_SETTING);
    switch (option) {
      case ALL:
        return true;
      case OFF:
        return false;
      case FRIENDS:
        return Friends.isFriend(from, to.getId());
    }
    return true;
  }

  public static Optional<User> getLastMessaged(UUID user) {
    if (user == null) {
      return Optional.of(User.CONSOLE);
    }

    return Optional.ofNullable(lastMessages.get(user));
  }

  @Override
  public PrivateMessage readAsync(JsonObject json) {
    int fromId = json.get("from").getAsInt();
    int toId = json.get("to").getAsInt();
    int fromCat = json.get("category").getAsInt();
    String body = json.get("body").getAsString();

    Optional<ServerCategory> category = Optional.ofNullable(
        Magma.get().database().getServerCategories().select().where("id", fromCat).execute()
            .first());
    Optional<ServerCategory> local = Magma.get().localServer()
        .getCategory(Magma.get().database().getServerCategories());
    if (category.isPresent() && local.isPresent()) {
      if (!local.get().equals(category.get())) {
        if (!local.get().getOptions().isExternalPMs() || !category.get().getOptions()
            .isPublishPMs()) {
          return null;
        }
      }
    }

    User from;
    if (fromId == 0) {
      from = User.CONSOLE;
    } else {
      from = Hook.database().getUsers().findById(fromId).orElse(null);
    }

    User to;
    if (fromId == 0) {
      to = User.CONSOLE;
    } else {
      to = Hook.database().getUsers().findById(toId).orElse(null);
    }

    // Todo: Better handling
    if (from == null || to == null) {
      throw new RuntimeException();
    }

    return new PrivateMessage(from, to, body);
  }

  @Override
  public void handle(PrivateMessage message) {
    lastMessages.put(message.getFrom().getUniqueId(), message.getTo());
    lastMessages.put(message.getTo().getUniqueId(), message.getFrom());

    CommandSender from = Users.player(message.getFrom()).orElse(null);
    CommandSender to = Users.player(message.getTo()).orElse(null);

    if (message.getFrom().getId() == 0) {
      from = Bukkit.getConsoleSender();
    }

    if (message.getTo().getId() == 0) {
      to = Bukkit.getConsoleSender();
    }

    Localizable body = new UnlocalizedText(message.getBody(), ChatColor.WHITE);

    if (from != null) {
      Localizable toName = Users.getLocalizedDisplay(message.getTo());
      from.sendMessage(Messages.GENERIC_MESSAGE_TO.with(ChatColor.GRAY, toName, body));
    }

    if (to != null) {
      // Check if player allows messages from that user
      if (shouldDeliver(message.getFrom(), message.getTo())) {
        Localizable fromName = Users.getLocalizedDisplay(message.getFrom(), true);
        to.sendMessage(Messages.GENERIC_MESSAGE_FROM.with(ChatColor.GRAY, fromName, body));
        if (to instanceof Player) {
          SoundEvent call = Events
              .call(new SoundEvent((Player) to, SoundType.SNARE, SoundLocation.PRIVATE_MESSAGE));
          call.getSound().play((Player) to, 1F);
        }
      }
    }
  }

  public enum PrivateMessageScope {
    ALL,
    FRIENDS,
    OFF
  }

  @Data
  public static class PrivateMessage implements RedisMessage {

    private final User from;
    private final User to;
    private final String body;

    @Override
    public String channel() {
      return "private-msg";
    }

    @Override
    public JsonObject write() {
      JsonObject json = new JsonObject();
      json.addProperty("from", this.from.getId());
      json.addProperty("to", this.to.getId());
      json.addProperty("body", this.body);
      json.addProperty("category", Magma.get().localServer().getServerCategoryId());
      return json;
    }
  }
}
