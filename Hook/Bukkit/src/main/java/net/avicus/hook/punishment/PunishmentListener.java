package net.avicus.hook.punishment;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.hook.Hook;
import net.avicus.hook.HookConfig;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.IPBan;
import net.avicus.magma.database.model.impl.Punishment;
import net.avicus.magma.database.model.impl.Session;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.event.user.AsyncHookLoginEvent;
import net.avicus.magma.network.user.Users;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PunishmentListener implements Listener {

  private static final List<String> BLACKLIST = Arrays
      .asList("/t ", "/g ", "/global ", "/team ", "/message ", "/ msg", "/premium ", "/pr ",
          "/reply ", "/r ");

  @Getter
  private final List<UUID> muted = Lists.newArrayList();

  @EventHandler(priority = EventPriority.LOWEST)
  public void noBanEvasion(AsyncHookLoginEvent event) {
    if (!event.isNewUser()) {
      return;
    }
    final User created = event.getUser();
    Optional<Session> last = Hook.database().getSessions()
        .findLatestByIp(event.getLoginEvent().getAddress().getHostAddress());
    HookTask.of(() -> {
      last.ifPresent(session -> {
        User u = session.getUser(Hook.database());
        u.punishments(Hook.database(), Optional.empty()).stream().filter(Punishment::prohibitLogin)
            .findFirst().ifPresent(punishment -> {
          Punishment p = new Punishment(created.getId(), User.CONSOLE.getId(), Punishment.Type.BAN,
              "[Auto] Ban Evasion - " + u.getName(), new Date(), Optional.empty(), false, false,
              Hook.server().getId());
          Punishments.broadcast(p);
          Hook.database().getPunishments().insert(p).execute();

          Optional<Player> player = Users.player(created);
          player.ifPresent(player1 -> {
            Localizable message = Punishments.formatKick(player1.getLocale(), p);
            event.setKickMessage(message);
            event.setCancelled(true);
          });

          if (HookConfig.Punishments.isRedis()) {
            Hook.redis().publish(new PunishmentHandler.PunishmentMessage(Hook.server(), p));
          }
        });
      });
    }).nowAsync();
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerLogin(AsyncHookLoginEvent event) {
    final User user = event.getUser();
    List<Punishment> punishments = user.punishments(Hook.database(),
        Magma.get().localServer().getCategory(Magma.get().database().getServerCategories()));

    muted.remove(user.getUniqueId());

    for (Punishment punishment : punishments) {
      if (punishment.prohibitLogin()) {
        Locale locale = user.getLocale();
        event.setKickMessage(Punishments.formatKick(locale, punishment));
        event.setCancelled(true);
        return;
      }
      if (punishment.mutePlayer()) {
        muted.add(user.getUniqueId());
      }
    }

    Optional<IPBan> ipBan = Hook.database().getIpBans()
        .getByIp(event.getLoginEvent().getAddress().getHostAddress());
    if (ipBan.isPresent() && ipBan.get().isEnabled() && !ipBan.get().isUserExcluded(user.getId())) {
      String message =
          Messages.PUNISHMENT_IP_BAN.with(ChatColor.RED).translate(user.getLocale()).toLegacyText()
              + "\n" + ChatColor.GRAY + "(" + ChatColor.GOLD + ipBan.get().getReason()
              + ChatColor.GRAY + ")";
      event.setKickMessage(new UnlocalizedText(message));
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void enforceMute(AsyncPlayerChatEvent event) {
    String message = event.getMessage().toLowerCase().trim();

    if (message.startsWith("/")) {
      boolean allow = true;
      for (String cmd : BLACKLIST) {
        if (message.startsWith(cmd)) {
          allow = false;
        }
      }

      if (allow) {
        return; // Allow all commands except team/global chat
      }
    }

    if (muted.contains(event.getPlayer().getUniqueId())) {
      event.setCancelled(true);
    }
  }
}
