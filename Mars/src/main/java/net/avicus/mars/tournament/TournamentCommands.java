package net.avicus.mars.tournament;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import java.util.Optional;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.command.exception.CommandMatchException;
import net.avicus.atlas.countdown.CyclingCountdown;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.util.Messages;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import net.avicus.mars.CompetitiveEvent;
import net.avicus.mars.MarsPlugin;
import net.avicus.mars.MarsTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.joda.time.Seconds;

public class TournamentCommands {

  @CommandPermissions("mars.tm.register")
  @Command(aliases = {
      "register"}, desc = "Toggle a team's registration for this math.", min = 1, max = -1, usage = "<team>")
  public static void register(CommandContext cmd, CommandSender sender) throws CommandException {
    TournamentMatch tournamentMatch = getEvent();

    String query = cmd.getJoinedStrings(0);

    Bukkit.getServer().getScheduler().runTaskAsynchronously(MarsPlugin.getInstance(), () -> {
      MarsTeam team = MarsPlugin.getInstance().createTeam(query).orElse(null);

      if (team == null || !Magma.get().database().getTournaments()
          .isTeamPlaying(tournamentMatch.getTournament(),
              Magma.get().database().getTeams().findById(team.getId()).get())) {
        sender.sendMessage(ChatColor.RED + "No registered team matched query.");
        return;
      }

      boolean registered = tournamentMatch.toggleRegisterTeam(team);

      if (registered) {
        sender.sendMessage(
            ChatColor.YELLOW + "You have registered " + team.getName() + " for this match.");
      } else {
        sender.sendMessage(
            ChatColor.YELLOW + "You have unregistered " + team.getName() + " for this match.");
      }
    });
  }

  @CommandPermissions("mars.tm.cycle")
  @Command(aliases = {
      "cycle"}, desc = "Cycle to the next map in the rotation, keeping the teams and re-assigning users.")
  public static void cycle(CommandContext cmd, CommandSender sender) throws CommandException {
    Match match = Atlas.getMatch();
    TournamentMatch tournamentMatch = getEvent();
    if (match == null) {
      throw new CommandMatchException();
    }

    if (!Atlas.get().getMatchManager().getRotation().getNextMatch().isPresent()) {
      sender.sendMessage(Messages.ERROR_NO_MATCHES.with(ChatColor.RED));
      return;
    }

    Match next = Atlas.get().getMatchManager().getRotation().getNextMatch().get();
    CyclingCountdown countdown = new CyclingCountdown(match, next,
        Optional.of(Seconds.seconds(15).toStandardDuration()));
    Atlas.get().getMatchManager().getRotation().cycleMatch(countdown);
    Bukkit.getScheduler().runTaskLater(MarsPlugin.getInstance(), () -> {
      tournamentMatch.copyAndSetup(next);
    }, 17 * 20);
  }

  @CommandPermissions("mars.tm.ignore")
  @Command(aliases = {
      "ignore"}, desc = "Ignore a user from being forced onto teams.", min = 1, max = 1, usage = "<player>")
  public static void ignore(CommandContext cmd, CommandSender sender) throws CommandException {
    TournamentMatch tournamentMatch = getEvent();

    String query = cmd.getJoinedStrings(0);

    Bukkit.getServer().getScheduler().runTaskAsynchronously(MarsPlugin.getInstance(), () -> {
      User user = Magma.get().database().getUsers().findByName(query).orElse(null);

      if (user == null) {
        sender.sendMessage(ChatColor.RED + "No player matched query.");
        return;
      }

      boolean ignored = tournamentMatch.toggleIgnoreUser(user);
      if (ignored) {
        sender.sendMessage(ChatColor.YELLOW + "You have ignored " + user.getName()
            + " from being a part of this event.");
      } else {
        sender.sendMessage(ChatColor.YELLOW + "You have un-ignored " + user.getName()
            + " from being a part of this event.");
      }
    });
  }

  @CommandPermissions("mars.tm.reset")
  @Command(aliases = {"reset"}, desc = "Reset to the default match state.")
  public static void reset(CommandContext cmd, CommandSender sender) throws CommandException {
    TournamentMatch tournamentMatch = getEvent();
    tournamentMatch.reset();
    sender.sendMessage(ChatColor.GREEN + "Match reset.");
  }

  @CommandPermissions("mars.tm.ready")
  @Command(aliases = {"ready"}, desc = "Toggle readiness of your team.")
  public static void ready(CommandContext cmd, CommandSender sender) throws CommandException {
    TournamentMatch tournamentMatch = getEvent();

    for (MarsTeam team : tournamentMatch.getTeams()) {
      if (team.isMember(Users.fromSender(sender).getId())) {
        boolean ready = tournamentMatch.toggleReadyTeam(team);
        if (ready) {
          Bukkit.broadcastMessage(ChatColor.GOLD + team.getName() + " is now marked as ready.");
        } else {
          Bukkit
              .broadcastMessage(ChatColor.GOLD + team.getName() + "is no longer marked as ready.");
        }
        return;
      }
    }

    // Not on a team, check if has ref permission
    if (sender.hasPermission("mars.tm.refready")) {
      boolean ready = tournamentMatch.isRefsReady();
      tournamentMatch.setRefsReady(!ready);
      if (!ready) {
        Bukkit.broadcastMessage(ChatColor.GOLD + "The referees are now marked as ready.");
      } else {
        Bukkit.broadcastMessage(ChatColor.GOLD + "The referees are no longer marked as ready.");
      }
    }
  }

  private static TournamentMatch getEvent() throws CommandException {
    CompetitiveEvent event = MarsPlugin.getInstance().getEvents().getCurrentEvent().orElse(null);

    if (!(event instanceof TournamentMatch)) {
      throw new CommandException("There is no tournament currently active.");
    }

    TournamentMatch tournamentMatch = (TournamentMatch) event;

    return tournamentMatch;
  }

  public static class TournamentParentCommand {

    @Command(aliases = {"tournament",
        "tm"}, usage = "<register>|<reset>|<ready>|<ignore>", desc = "Manage tournament matches.", min = 1)
    @NestedCommand(TournamentCommands.class)
    public static void tournament(CommandContext cmd, CommandSender sender) {
      // Never called
    }
  }
}
