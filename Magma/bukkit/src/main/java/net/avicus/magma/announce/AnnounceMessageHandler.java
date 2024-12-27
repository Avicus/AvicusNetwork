package net.avicus.magma.announce;

import com.google.gson.JsonObject;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.Getter;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.redis.RedisMessage;
import net.avicus.magma.util.AsyncRedisHandler;
import net.avicus.magma.util.MagmaTranslations;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.joda.time.Instant;

public class AnnounceMessageHandler extends
    AsyncRedisHandler<AnnounceMessageHandler.AnnounceMessage> {

  private static TextComponent prefixNormal = new TextComponent(
      ChatColor.GOLD + "[" + ChatColor.AQUA + ChatColor.BOLD + "AVN" + ChatColor.GOLD + "] ");
  private static TextComponent prefixCritical = new TextComponent(
      ChatColor.RED + "[" + ChatColor.GOLD + ChatColor.BOLD + "NETWORK ALERT" + ChatColor.RED
          + "] ");

  protected AnnounceMessage lastMessage;
  protected Instant lastMessageReceived;

  public AnnounceMessageHandler() {
    super(new String[]{"announce"});
  }

  @Override
  public void handle(AnnounceMessage message) {
    this.lastMessage = message;
    this.lastMessageReceived = Instant.now();
    broadcast(message, null);
  }

  protected void broadcast(AnnounceMessage message, @Nullable Player who) {
    TextComponent prefix;
    if (message.getType() == AnnounceType.NO_PREFIX) {
      prefix = new TextComponent();
    } else if (message.getType() == AnnounceType.CRITICAL) {
      prefix = prefixCritical;
    } else {
      prefix = prefixNormal;
    }

    BaseComponent send = new TextComponent(prefix, new TextComponent(message.getMessage()));

    if (message.getType() == AnnounceType.JOIN && message.getServer().isPresent()) {
      Localizable connect = MagmaTranslations.RTP_SERVER_CONNECT
          .with(message.getServer().get().getName());
      if (who != null) {
        BaseComponent connectTranslated = connect.render(who);
        connectTranslated.setColor(net.md_5.bungee.api.ChatColor.BLUE);
        connectTranslated.setUnderlined(true);
        connectTranslated.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
            "/server " + message.getServer().get().getName()));
        who.sendMessage(new TextComponent(send, new TextComponent(" "), connectTranslated));
      } else {
        Bukkit.getOnlinePlayers().forEach(p -> {
          BaseComponent connectTranslated = connect.render(p);
          connectTranslated.setColor(net.md_5.bungee.api.ChatColor.BLUE);
          connectTranslated.setUnderlined(true);
          connectTranslated.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
              "/server " + message.getServer().get().getName()));
          p.sendMessage(new TextComponent(send, new TextComponent(" "), connectTranslated));
        });
      }
    } else {
      if (who != null) {
        who.sendMessage(send);
      } else {
        Bukkit.broadcast(send);
      }
    }
  }

  @Override
  public AnnounceMessage readAsync(JsonObject json) {
    boolean legacy = json.get("from_legacy").getAsBoolean();

    String message = json.get("message").getAsString();
    final BaseComponent[] components;

    if (legacy) {
      components = TextComponent.fromLegacyText(message);
    } else {
      components = ComponentSerializer.parse(message);
    }

    Optional<Server> server = Optional.empty();
    if (json.has("server_id")) {
      int serverId = json.get("server_id").getAsInt();
      server = Magma.get().database().getServers().findById(serverId);
    }

    AnnounceType type = AnnounceType.valueOf(json.get("type").getAsString());
    return new AnnounceMessage(components, type, legacy, server);
  }

  public enum AnnounceType {
    NO_PREFIX, MESSAGE, JOIN, CRITICAL;
  }

  public static class AnnounceMessage implements RedisMessage {

    @Getter
    private final BaseComponent[] message;
    @Getter
    private final AnnounceType type;
    @Getter
    private final boolean fromLegacy;
    @Getter
    private final Optional<Server> server;

    public AnnounceMessage(BaseComponent[] message,
        AnnounceType type, Server server) {
      this(message, type, false, Optional.ofNullable(server));
    }

    public AnnounceMessage(BaseComponent[] message,
        AnnounceType type, boolean fromLegacy, Optional<Server> server) {
      this.message = message;
      this.type = type;
      this.fromLegacy = fromLegacy;
      this.server = server;
    }

    @Override
    public String channel() {
      return "announce";
    }

    @Override
    public JsonObject write() {
      JsonObject json = new JsonObject();
      server.ifPresent(s -> json.addProperty("server_id", s.getId()));
      json.addProperty("type", this.type.name());
      json.addProperty("message", ComponentSerializer.toString(this.message));
      json.addProperty("from_legacy", this.fromLegacy);
      return json;
    }
  }
}
