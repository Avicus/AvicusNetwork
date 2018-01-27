package net.avicus.hook.commands;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.avicus.compendium.Time;
import net.avicus.hook.utils.UserUtils;
import net.avicus.hook.wrapper.DiscordUser;
import net.avicus.magma.NetworkIdentification;
import net.avicus.magma.database.model.impl.Punishment;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.database.table.impl.PunishmentTable;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;

public class PunishmentCommads {

  private static Optional<User> checkPermissions(DiscordCommand.CommandContext context) {
    Optional<User> registered = context.getHook().getUserManagementService()
        .getUser(context.getSender().getIdLong());
    if (!registered.isPresent()) {
      context.getLocation().sendMessage("You must be registered to use this command!")
          .complete();
    } else if (!UserUtils.hasRoleOrHigher(context.getSender(),
        context.getGuild().getRolesByName("Moderator", false).get(0))) {
      context.getLocation()
          .sendMessage("**Error:** You do not have permission to use this command!")
          .complete();
      return Optional.empty();
    }

    return registered;
  }

  private static void punish(DiscordCommand.CommandContext context, int user, int staff,
      String reason, Punishment.Type type, Optional<Date> expires) {
    PunishmentTable table = context.getHook().getDatabase().getPunishments();
    table.insert(
        new Punishment(user, staff, type, reason,
            new Date(), expires, false, false, 0)
    ).execute();
  }

  private static Optional<DiscordUser> gatherUser(DiscordCommand.CommandContext context,
      String username) {
    Optional<User> query = context.getHook().getDatabase().getUsers().findByName(username);
    if (!query.isPresent()) {
      context.getLocation().sendMessage("User _" + username + "_ not found!").complete();
      return Optional.empty();
    }

    User found = query.get();

    Optional<DiscordUser> discord = context.getHook().getUserManagementService().getUser(found);
    if (!discord.isPresent()) {
      context.getLocation()
          .sendMessage("User _" + username + "_ is not registered with the discord!").complete();
      return Optional.empty();
    }

    return discord;
  }

  public static class Warn implements DiscordCommand {

    @Override
    public void execute(CommandContext context, List<String> args) throws Exception {
      Optional<User> possibleStaff = checkPermissions(context);

      if (!possibleStaff.isPresent()) {
        return;
      }

      User staff = possibleStaff.get();

      if (args.size() < 3) {
        context.getLocation().sendMessage("**Usage:** !warn <username> <reason>").complete();
        return;
      }

      Optional<DiscordUser> user = gatherUser(context, args.get(0));
      if (!user.isPresent()) {
        return;
      }

      String message = StringUtils.join(args.subList(1, args.size()), " ");

      DiscordUser found = user.get();

      punish(context, found.getUser().getId(), staff.getId(), message, Punishment.Type.DISCORD_WARN,
          Optional.empty());

      found.message("**WARNING:**");
      found.message("**WARNING:**");
      found.message("You have been warned by " + staff.getName() + " for " + message);
      found.message("**WARNING:**");
      found.message("**WARNING:**");

      context.getLocation().sendMessage("Warned " + found.getUser().getName() + " for " + message)
          .complete();
    }
  }

  public static class Kick implements DiscordCommand {

    @Override
    public void execute(CommandContext context, List<String> args) throws Exception {
      Optional<User> possibleStaff = checkPermissions(context);

      if (!possibleStaff.isPresent()) {
        return;
      }

      User staff = possibleStaff.get();

      if (args.size() < 3) {
        context.getLocation().sendMessage("**Usage:** !kick <username> <reason>").complete();
        return;
      }

      Optional<DiscordUser> user = gatherUser(context, args.get(0));
      if (!user.isPresent()) {
        return;
      }

      String message = StringUtils.join(args.subList(1, args.size()), " ");

      DiscordUser found = user.get();

      punish(context, found.getUser().getId(), staff.getId(), message, Punishment.Type.DISCORD_KICK,
          Optional.empty());

      found.message("You have been kicked by " + staff.getName() + " for " + message);
      found.message("You can appeal at " + NetworkIdentification.URL + "/appeal");

      context.getGuild().getController().kick(
          context.getGuild().getMember(found.getDiscordUser()),
          "Kicked by " + staff.getName() + " for " + message
      ).complete();
      context.getLocation().sendMessage("Kicked " + found.getUser().getName() + " for " + message)
          .complete();
    }
  }

  public static class TempBan implements DiscordCommand {

    @Override
    public void execute(CommandContext context, List<String> args) throws Exception {
      Optional<User> possibleStaff = checkPermissions(context);

      if (!possibleStaff.isPresent()) {
        return;
      }

      User staff = possibleStaff.get();

      if (args.size() < 4) {
        context.getLocation().sendMessage("**Usage:** !tempban <username> <time> <reason>")
            .complete();
        return;
      }

      Optional<DiscordUser> user = gatherUser(context, args.get(0));
      if (!user.isPresent()) {
        return;
      }

      Period length;
      try {
        length = context.getHook().getPeriodFormatter().parsePeriod(args.get(1));
      } catch (Exception e) {
        context.getLocation().sendMessage("Invalid peiod format specified!");
        return;
      }

      Date expiry = new DateTime().plus(length).toDate();

      String message = StringUtils.join(args.subList(2, args.size()), " ");

      DiscordUser found = user.get();

      punish(context, found.getUser().getId(), staff.getId(), message,
          Punishment.Type.DISCORD_TEMPBAN, Optional.of(expiry));

      String humanExpire = Time.prettyTime(Locale.ENGLISH).format(expiry);

      found.message("You have been temporarily banned by " + staff.getName() + " for " + message);
      found.message("The ban will expire in " + humanExpire);
      found.message("You can appeal at " + NetworkIdentification.URL + "/appeal");

      context.getGuild().getController().kick(
          context.getGuild().getMember(found.getDiscordUser()),
          "Temp banned by " + staff.getName() + " (" + humanExpire + ")" + " for " + message
      ).complete();
      context.getLocation().sendMessage(
          "Temp Banned " + found.getUser().getName() + "(" + humanExpire + ")" + " for " + message)
          .complete();
    }
  }

  public static class Ban implements DiscordCommand {

    @Override
    public void execute(CommandContext context, List<String> args) throws Exception {
      Optional<User> possibleStaff = checkPermissions(context);

      if (!possibleStaff.isPresent()) {
        return;
      }

      User staff = possibleStaff.get();

      if (args.size() < 3) {
        context.getLocation().sendMessage("**Usage:** !ban <username> <reason>").complete();
        return;
      }

      Optional<DiscordUser> user = gatherUser(context, args.get(0));
      if (!user.isPresent()) {
        return;
      }

      String message = StringUtils.join(args.subList(1, args.size()), " ");

      DiscordUser found = user.get();

      punish(context, found.getUser().getId(), staff.getId(), message, Punishment.Type.DISCORD_BAN,
          Optional.empty());

      found.message("You have been banned by " + staff.getName() + " for " + message);
      found.message("You can appeal at " + NetworkIdentification.URL + "/appeal");

      context.getGuild().getController().kick(
          context.getGuild().getMember(found.getDiscordUser()),
          "Banned by " + staff.getName() + " for " + message
      ).complete();
      context.getLocation().sendMessage("Banned " + found.getUser().getName() + " for " + message)
          .complete();
    }
  }
}
