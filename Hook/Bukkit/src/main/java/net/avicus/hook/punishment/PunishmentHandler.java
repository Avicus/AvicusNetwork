package net.avicus.hook.punishment;

import com.google.gson.JsonObject;
import java.util.Optional;
import lombok.Getter;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.hook.Hook;
import net.avicus.hook.punishment.PunishmentHandler.PunishmentMessage;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Punishment;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.ServerCategory;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.redis.RedisMessage;
import net.avicus.magma.util.AsyncRedisHandler;
import org.bukkit.entity.Player;

public class PunishmentHandler extends AsyncRedisHandler<PunishmentMessage> {

  protected PunishmentHandler() {
    super(new String[]{"punishment"});
  }

  @Override
  public PunishmentMessage readAsync(JsonObject json) {
    int serverId = json.get("server_id").getAsInt();
    Server server = Hook.database().getServers().findById(serverId).get();

    int punishmentId = json.get("punishment_id").getAsInt();
    Punishment punishment = Hook.database().getPunishments().findById(punishmentId).get();

    return new PunishmentMessage(server, punishment);
  }

  @Override
  public void handle(PunishmentMessage message) {
    Punishment punishment = message.getPunishment();

    Optional<ServerCategory> local = Magma.get().localServer()
        .getCategory(Magma.get().database().getServerCategories());
    Optional<ServerCategory> serverCategory = Magma.get().database().getServerCategories()
        .fromServer(message.getServer());
    if (serverCategory.isPresent() && local.isPresent()) {
      if (!local.get().equals(serverCategory.get())) {
        if (!local.get().getOptions().isExternalPunishments() || !serverCategory.get()
            .getOptions()
            .isPublishPunishments()) {
          return;
        }
      }
    }

    if (punishment.kickOnPunish()) {
      Optional<Player> player = Users.player(punishment.getUserId());

      if (player.isPresent()) {
        Localizable text = Punishments.formatKick(player.get().getLocale(), punishment);
        player.get().kickPlayer(text.render(player.get()).toLegacyText());
      }
    }

    if (!punishment.isSilent()) {
      if (punishment.getServerId() != Hook.server().getId()) {
        Punishments.broadcast(punishment);
      }
    }
  }

  public static class PunishmentMessage implements RedisMessage {

    @Getter
    private final Server server;
    @Getter
    private final Punishment punishment;

    public PunishmentMessage(Server server, Punishment punishment) {
      this.server = server;
      this.punishment = punishment;
    }

    @Override
    public String channel() {
      return "punishment";
    }

    @Override
    public JsonObject write() {
      JsonObject json = new JsonObject();
      json.addProperty("server_id", this.server.getId());
      json.addProperty("punishment_id", this.punishment.getId());
      return json;
    }
  }
}
