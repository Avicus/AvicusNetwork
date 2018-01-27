package net.avicus.hook.punishment;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandNumberFormatException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.Setter;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.LocalizedTime;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.utils.Strings;
import net.avicus.hook.Hook;
import net.avicus.hook.HookConfig;
import net.avicus.hook.punishment.PunishmentHandler.PunishmentMessage;
import net.avicus.hook.utils.Commands;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Punishment;
import net.avicus.magma.database.model.impl.Punishment.Type;
import net.avicus.magma.database.model.impl.ServerCategory;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.github.paperspigot.Title;
import org.joda.time.DateTime;
import org.joda.time.Period;

public class PunishmentCommands {

  @Setter
  private static PunishmentListener LISTENER;

  private static int getStaffId(CommandSender sender) throws CommandException {
    if (sender instanceof ConsoleCommandSender) {
      return 0;
    } else if (sender instanceof Player) {
      return Hook.database().getUsers().findByUuid(((Player) sender).getUniqueId()).get()
          .getId();
    }
    throw new CommandException("Invalid command sender.");
  }

  public static void punish(String username, CommandSender sender, Punishment.Type type,
      String reason, Optional<Date> expiry, boolean silent, boolean record)
      throws CommandException {
    if (silent && !sender.hasPermission("hook.punish.silent")) {
      throw new CommandPermissionsException();
    }

    if (!record && !sender.hasPermission("hook.punish.norecord")) {
      throw new CommandPermissionsException();
    }

    new HookTask() {
      @Override
      public void run() throws Exception {
        Optional<User> user = Hook.database().getUsers().findByName(username);

        int staff = getStaffId(sender);

        if (!user.isPresent()) {
          sender.sendMessage(Messages.ERROR_NO_PLAYERS.with(ChatColor.RED));
          return;
        }

        Punishment punishment = new Punishment(user.get().getId(), staff, type, reason, new Date(),
            expiry, false, silent, Hook.server().getId());

        if (!silent) {
          Punishments.broadcast(punishment);
        }

        Optional<ServerCategory> local = Magma.get().localServer()
            .getCategory(Magma.get().database().getServerCategories());

        boolean reallyRecord = record;

        if (reallyRecord) {
          if (local.isPresent()) {
            switch (punishment.getType()) {
              case WARN:
                reallyRecord = local.get().getOptions().isRecordWarns();
                break;
              case TEMPBAN:
                reallyRecord = local.get().getOptions().isRecordTempBans();
                break;
              case KICK:
                reallyRecord = local.get().getOptions().isRecordKicks();
                break;
              case BAN:
                reallyRecord = local.get().getOptions().isRecordBans();
            }
          }
        }

        if (reallyRecord) {
          Hook.database().getPunishments().insert(punishment).execute();
        }

        if (punishment.kickOnPunish()) {
          Optional<Player> player = Users.player(user.get());

          player.ifPresent(player1 -> new HookTask() {
            @Override
            public void run() throws Exception {
              Localizable message = Punishments.formatKick(player1.getLocale(), punishment);
              player1.kickPlayer(message.translate(player1.getLocale()).toLegacyText());
            }
          }.now());
        }

        Users.player(user.get()).ifPresent(toWarn -> {
          toWarn.sendTitle(new Title("",
              Punishments.formatBroadcast(toWarn.getLocale(), punishment).translate(toWarn)
                  .toLegacyText()));
          toWarn.playSound(toWarn.getLocation(), Sound.ANVIL_BREAK, 1, 0.5f);
          HookTask.of(() -> {
            if (!toWarn.hasPotionEffect(PotionEffectType.CONFUSION)) {
              toWarn.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 4, 1));
            }
          }).now();
          if (type == Type.MUTE) {
            LISTENER.getMuted().add(toWarn.getUniqueId());
          }
        });

        sender.sendMessage(Messages.PUNISHMENT_SUCCESS.with(ChatColor.GREEN));

        // Can't broadcast punishments that aren't recorded.
        if (HookConfig.Punishments.isRedis() && !silent && record) {
          Hook.redis().publish(new PunishmentMessage(Hook.server(), punishment));
        }
      }
    }.nowAsync();
  }

  @CommandPermissions("hook.mute")
  @Command(aliases = {"mute",
      "m"}, desc = "Mute a player.", usage = "<player> <duration> <reason>", min = 3, flags = "ns")
  public static void mute(CommandContext context, CommandSender sender) throws CommandException {
    String username = context.getString(0);
    Period period = Commands.parsePeriod(context.getString(1));
    String reason = context.getJoinedStrings(2);

    Date expiry = new DateTime().plus(period).toDate();
    punish(username, sender, Type.MUTE, reason, Optional.of(expiry), context.hasFlag('s'),
        !context.hasFlag('n'));
  }

  @CommandPermissions("hook.warn")
  @Command(aliases = {"warn",
      "w"}, desc = "Warn a player.", usage = "<player> <reason>", min = 2, flags = "ns")
  public static void warn(CommandContext context, CommandSender sender) throws CommandException {
    String username = context.getString(0);
    String reason = context.getJoinedStrings(1);
    punish(username, sender, Punishment.Type.WARN, reason, Optional.empty(), context.hasFlag('s'),
        !context.hasFlag('n'));
  }

  @CommandPermissions("hook.kick")
  @Command(aliases = {"kick",
      "k"}, desc = "Kick a player.", usage = "<player> <reason>", min = 2, flags = "ns")
  public static void kick(CommandContext context, CommandSender sender) throws CommandException {
    String username = context.getString(0);
    String reason = context.getJoinedStrings(1);
    punish(username, sender, Punishment.Type.KICK, reason, Optional.empty(), context.hasFlag('s'),
        !context.hasFlag('n'));
  }

  @CommandPermissions("hook.ban")
  @Command(aliases = {"ban",
      "b"}, desc = "Permanently ban a player.", usage = "<player> <reason>", min = 2, flags = "nst:")
  public static void ban(CommandContext context, CommandSender sender) throws Exception {
    String username = context.getString(0);
    String reason = context.getJoinedStrings(1);

    if (context.hasFlag('t')) {
      if (!sender.hasPermission("hook.tempban")) {
        throw new CommandPermissionsException();
      }

      Period period = Commands.parsePeriod(context.getFlag('t'));

      Date expiry = new DateTime().plus(period).toDate();

      punish(username, sender, Punishment.Type.TEMPBAN, reason, Optional.of(expiry),
          context.hasFlag('s'), !context.hasFlag('n'));
    } else {
      punish(username, sender, Punishment.Type.BAN, reason, Optional.empty(),
          context.hasFlag('s'),
          !context.hasFlag('n'));
    }
  }

  @CommandPermissions("hook.tempban")
  @Command(aliases = {"tempban",
      "tb"}, desc = "Temporarily ban a player.", usage = "<player> <duration> <reason>", min = 3, flags = "ns")
  public static void tempban(CommandContext context, CommandSender sender) throws CommandException {
    String username = context.getString(0);
    Period period = Commands.parsePeriod(context.getString(1));
    String reason = context.getJoinedStrings(2);

    Date expiry = new DateTime().plus(period).toDate();

    punish(username, sender, Punishment.Type.TEMPBAN, reason, Optional.of(expiry),
        context.hasFlag('s'), !context.hasFlag('n'));
  }

  @CommandPermissions("hook.appeal")
  @Command(aliases = {
      "appeal"}, desc = "Appeal a punishment.", usage = "<id>", min = 1, flags = "d")
  public static void appeal(CommandContext context, CommandSender sender)
      throws CommandNumberFormatException {
    int id = context.getInteger(0);

    List<Punishment> list = Hook.database().getPunishments().select().where("id", id).execute();

    if (list.isEmpty()) {
      sender.sendMessage(Messages.ERROR_NO_PUNISHMENTS.with(ChatColor.RED));
      return;
    }

    new HookTask() {
      @Override
      public void run() throws Exception {
        LocalizedNumber number = new LocalizedNumber(id);

        if (context.hasFlag('d')) {
          Hook.database().getPunishments().delete().where("id", id).execute();
          sender.sendMessage(Messages.GENERIC_APPEALED_DELETED.with(ChatColor.GOLD, number));
        } else {
          Hook.database().getPunishments().update().where("id", id).set("appealed", true).execute();
          sender.sendMessage(Messages.GENERIC_APPEALED.with(ChatColor.GOLD, number));
        }
      }
    }.nowAsync();
  }

  @CommandPermissions("hook.lookup")
  @Command(aliases = {"lookup", "lu",
      "history"}, desc = "View a user's punishments.", usage = "<player>", min = 1, max = 1)
  public static void lookup(CommandContext cmd, CommandSender sender) {
    final Locale locale = sender.getLocale();
    HookTask.of(() -> {
      Optional<User> user = Hook.database().getUsers().findByName(cmd.getString(0));

      if (!user.isPresent()) {
        sender.sendMessage(Messages.ERROR_NO_PLAYERS.with(ChatColor.RED));
        return;
      }

      List<Punishment> punishments = Hook.database().getPunishments().findByUser(user.get());
      punishments.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));

      if (punishments.isEmpty()) {
        sender.sendMessage(Messages.ERROR_CLEAN_HISTORY.with(ChatColor.RED));
        return;
      }

      sender.sendMessage(Strings.padChatComponent(
          Messages.UI_PLAYER_HISTORY.with(ChatColor.GREEN, user.get().getName())
              .translate(sender.getLocale()), "-", ChatColor.YELLOW, ChatColor.AQUA));

      for (Punishment punishment : punishments) {
        if (punishment.isAppealed()) {
          continue;
        }

        UnlocalizedFormat format = new UnlocalizedFormat("{0} ({1})");
        sender.sendMessage(format.with(Punishments.formatBroadcast(locale, punishment),
            new LocalizedTime(punishment.getDate())));
      }
    }).nowAsync();
  }
}
