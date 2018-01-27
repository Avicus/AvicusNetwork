package net.avicus.hook.punishment;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import net.avicus.compendium.Time;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.compendium.settings.Setting;
import net.avicus.compendium.settings.types.SettingTypes;
import net.avicus.hook.Hook;
import net.avicus.hook.HookConfig;
import net.avicus.hook.utils.Events;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.NetworkIdentification;
import net.avicus.magma.database.model.impl.Punishment;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.joda.time.Duration;
import org.joda.time.Instant;

public class Punishments {

  private static final Setting<PunishmentAlertScope> PUNISHMENT_ALERTS_SETTING = new Setting<>(
      "punishment-alerts",
      SettingTypes.enumOf(PunishmentAlertScope.class),
      PunishmentAlertScope.SERVER,
      Messages.SETTINGS_PUNISHMENT_ALERT,
      Messages.SETTINGS_PUNISHMENT_ALERT_SUMMARY
  );

  public static void init(CommandsManagerRegistration cmds) {
    if (HookConfig.Punishments.isEnabled()) {
      PlayerSettings.register(PUNISHMENT_ALERTS_SETTING);
      cmds.register(PunishmentCommands.class);
      PunishmentListener listener = new PunishmentListener();
      PunishmentCommands.setLISTENER(listener);
      Events.register(listener);
      if (HookConfig.Punishments.isRedis()) {
        Hook.redis().register(new PunishmentHandler());
      }
    }
  }

  public static void broadcast(Punishment punishment) {
    boolean server = punishment.getServerId() == Hook.server().getId();

    for (Player player : Bukkit.getOnlinePlayers()) {
      User user = Users.user(player);
      PunishmentAlertScope option = PlayerSettings.get(player, PUNISHMENT_ALERTS_SETTING);

      if (user.getId() == punishment.getUserId() || option == PunishmentAlertScope.GLOBAL || (
          option == PunishmentAlertScope.SERVER && server)) {
        player.sendMessage(formatBroadcast(player.getLocale(), punishment));
      }
    }

    Bukkit.getConsoleSender().sendMessage(formatBroadcast(Locale.US, punishment));
  }

  public static Localizable formatBroadcast(Locale locale, Punishment punishment) {
    Localizable staff = Users.getLocalizedDisplay(punishment.getStaff(Hook.database()));
    Localizable type;
    Date expiracy = null;
    switch (punishment.getType()) {
      case MUTE:
        type = Messages.PUNISHMENT_MUTED.with();
        break;
      case WARN:
        type = Messages.PUNISHMENT_WARNED.with();
        break;
      case KICK:
        type = Messages.PUNISHMENT_KICKED.with();
        break;
      case TEMPBAN:
        type = Messages.PUNISHMENT_TEMPBANNED.with();
        expiracy = punishment.getExpiry().get();
        break;
      case BAN:
        type = Messages.PUNISHMENT_BANNED.with();
        break;
      case WEB_BAN:
        type = Messages.PUNISHMENT_WEB_BANNED.with();
        break;
      case WEB_TEMPBAN:
        type = Messages.PUNISHMENT_WEB_TEMPBANNED.with();
        break;
      case TOURNAMENT_BAN:
        type = Messages.PUNISHMENT_TOURNAMENT_BANNED.with();
        break;
      case DISCORD_WARN:
        type = Messages.PUNISHMENT_DISCORD_WARNED.with();
        break;
      case DISCORD_KICK:
        type = Messages.PUNISHMENT_DISCORD_KICKED.with();
        break;
      case DISCORD_TEMPBAN:
        type = Messages.PUNISHMENT_DISCORD_TEMPBANNED.with();
        break;
      case DISCORD_BAN:
        type = Messages.PUNISHMENT_DISCORD_BANNED.with();
        break;
      default:
        throw new RuntimeException("Unknown punishment type \"" + punishment + "\".");
    }

    Localizable user = Users.getLocalizedDisplay(punishment.getUser(Hook.database()));
    Localizable reason = new UnlocalizedText(punishment.getReason());
    Optional<Localizable> period = Optional.empty();

    if (expiracy != null) {
      Date then = new Date();
      then.setTime(then.getTime() + new Duration(Instant.now(), new Instant(expiracy.getTime()))
          .getMillis());
      period = Optional
          .of(new UnlocalizedText(Time.removeFutureSuffix(Time.prettyTime(locale)).format(then)));
    }

    staff.style().color(ChatColor.WHITE);
    type.style().color(ChatColor.RED);
    if (period.isPresent()) {
      period.get().style().color(ChatColor.DARK_RED);
    }
    user.style().color(ChatColor.WHITE);
    reason.style().color(ChatColor.GOLD);

    if (period.isPresent()) {
      return Messages.PUNISHMENT_BROADCAST_TIME
          .with(ChatColor.GOLD, staff, type, period.get(), user, reason);
    }

    return Messages.PUNISHMENT_BROADCAST.with(ChatColor.GOLD, staff, type, user, reason);
  }

  public static Localizable formatKick(Locale locale, Punishment punishment) {
    Localizable ban = formatBroadcast(locale, punishment);
    Localizable website = new UnlocalizedText(NetworkIdentification.URL + "/appeal",
        ChatColor.YELLOW);
    Localizable appeal = Messages.PUNISHMENT_APPEAL.with(ChatColor.GOLD, website);
    LocalizableFormat format = new UnlocalizedFormat("{0}\n\n{1}");

    return format.with(ban, appeal);
  }

  public enum PunishmentAlertScope {
    GLOBAL,
    SERVER,
    NONE
  }
}
