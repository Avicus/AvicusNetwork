package net.avicus.hook.commands;

import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.avicus.hook.utils.UserUtils;
import net.avicus.magma.database.model.impl.Punishment;
import net.avicus.magma.database.model.impl.Session;
import net.avicus.magma.database.model.impl.User;
import net.avicus.quest.query.Operator;

public class AltCheckCommand implements DiscordCommand {

  @Override
  public void execute(CommandContext context, List<String> args) throws Exception {
    if (!UserUtils.hasRoleOrHigher(context.getSender(),
        context.getGuild().getRolesByName("Moderator", false).get(0))) {
      context.getLocation()
          .sendMessage("**Error:** You do not have permission to use this command!").complete();
      return;
    }

    if (args.isEmpty() || args.size() > 1) {
      context.getLocation().sendMessage("**Usage:** !altcheck <username>").complete();
      return;
    }

    String username = args.get(0);

    Optional<User> query = context.getHook().getDatabase().getUsers().findByName(username);
    if (!query.isPresent()) {
      context.getLocation().sendMessage("User _" + username + "_ not found!").complete();
      return;
    }

    User found = query.get();

    Session last = context.getHook().getDatabase().getSessions().findLatest(found.getId())
        .orElse(null);
    if (last == null) {
      context.getLocation().sendMessage(context.getHook()
          .generateRichMessage("Alt Check", "User has no banned alts!", Color.GREEN)).complete();
      return;
    }

    List<Session> sessionsAll = context.getHook().getDatabase().getSessions().select()
        .where("ip", last.getIp()).where("user_id", found.getId(), Operator.NOT_EQUAL).execute();
    Set<Integer> alts = sessionsAll.stream().map(Session::getUserId).collect(Collectors.toSet());
    boolean banned = false;
    String bannedName = "";
    for (Integer userId : alts) {
      User user = context.getHook().getDatabase().getUsers().findById(userId).orElse(null);
      if (user == null) {
        continue;
      }

      banned = user.punishments(context.getHook().getDatabase(), Optional.empty()).stream()
          .anyMatch(Punishment::prohibitLogin);
      bannedName = user.getName();
      if (banned) {
        break;
      }
    }

    if (banned) {
      context.getLocation().sendMessage(context.getHook()
          .generateRichMessage("Alt Check", "User is ban evading from `" + bannedName + "`.",
              Color.RED)).complete();
    } else {
      context.getLocation().sendMessage(context.getHook()
          .generateRichMessage("Alt Check", "User has no banned alts!", Color.GREEN)).complete();
    }
  }
}
