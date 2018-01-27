package net.avicus.atlas.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.WrappedCommandException;
import java.util.List;
import java.util.Optional;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.command.exception.CommandMatchException;
import net.avicus.atlas.countdown.CyclingCountdown;
import net.avicus.atlas.countdown.StartingCountdown;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.StaticResultCheck;
import net.avicus.atlas.module.checks.types.TimeCheck;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.results.ResultsModule;
import net.avicus.atlas.module.results.scenario.EndScenario;
import net.avicus.atlas.module.results.scenario.ObjectivesScenario;
import net.avicus.atlas.module.results.scenario.TeamScenario;
import net.avicus.atlas.module.results.scenario.TieScenario;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.util.Messages;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.commands.exception.TranslatableCommandErrorException;
import net.avicus.compendium.number.NumberComparator;
import net.avicus.compendium.plugin.CompendiumPlugin;
import net.avicus.compendium.utils.Strings;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class StateCommands {

  private static final PeriodFormatter periodFormatter = new PeriodFormatterBuilder()
      .appendDays().appendSuffix("d")
      .appendHours().appendSuffix("h")
      .appendMinutes().appendSuffix("m")
      .appendSecondsWithOptionalMillis().appendSuffix("s")
      .appendSeconds()
      .toFormatter();

  @Command(aliases = "nextstate", desc = "Move to the next match state.", max = 0)
  @CommandPermissions("atlas.states.next")
  public static void next(CommandContext cmd, CommandSender sender) throws CommandException {
    Match match = Atlas.getMatch();
    if (match == null) {
      throw new CommandMatchException();
    }

    StatesModule module = match.getRequiredModule(StatesModule.class);

    if (!module.getNextState().isPresent()) {
      sender.sendMessage(Messages.ERROR_NO_STATES.with(ChatColor.RED));
      return;
    }

    CompendiumPlugin.getInstance().getCountdownManager().cancelAll();

    String before = module.getState().getId();
    module.next();
    String after = module.getState().getId();

    sender.sendMessage(Messages.GENERIC_STATE_CHANGE.with(ChatColor.GOLD, before, after));
  }

  @Command(aliases = "cycle", flags = "f", desc = "Move to a new match.", max = 1, usage = "(countdown)")
  @CommandPermissions("atlas.states.cycle")
  public static void cycle(CommandContext cmd, CommandSender sender) throws CommandException {
    Match match = Atlas.getMatch();
    if (match == null) {
      throw new CommandMatchException();
    }

    if (!Atlas.get().getMatchManager().getRotation().getNextMatch().isPresent()) {
      sender.sendMessage(Messages.ERROR_NO_MATCHES.with(ChatColor.RED));
      return;
    }

    boolean playing = match.getRequiredModule(StatesModule.class).isPlaying();

    boolean force = cmd.hasFlag('f');

    if (force && !sender.hasPermission("atlas.states.cycle.force")) {
      throw new CommandPermissionsException();
    }

    if (playing && !force) {
      throw new TranslatableCommandErrorException(Messages.ERROR_CYCLE_WHILE_PLAYING);
    }

    if (playing) {
      new TieScenario(match, new StaticResultCheck(CheckResult.ALLOW), 1).execute(match);
    }

    Optional<Duration> duration = Optional
        .of(match.getMap().getCountdownConfig().getDuration(CyclingCountdown.class));
    if (cmd.argsLength() == 1) {
      if (!sender.hasPermission("atlas.states.cycle.modifytime")) {
        throw new TranslatableCommandErrorException(Translations.ERROR_PERMISSION_MODTIME);
      }

      try {
        duration = Optional.of(Strings.toDuration(cmd.getString(0)));
      } catch (Exception e) {
        throw new WrappedCommandException(e);
      }
    }

    CyclingCountdown countdown = new CyclingCountdown(match,
        Atlas.get().getMatchManager().getRotation().getNextMatch().get(), duration);
    Atlas.get().getMatchManager().getRotation().cycleMatch(countdown);
  }

  @Command(aliases = "start", desc = "Start the match.", max = 1, usage = "(countdown)")
  @CommandPermissions("atlas.states.start")
  public static void start(CommandContext cmd, CommandSender sender) throws CommandException {
    Match match = Atlas.getMatch();
    if (match == null) {
      throw new CommandMatchException();
    }

    if (!match.getRequiredModule(StatesModule.class).isStarting()) {
      sender.sendMessage(Messages.ERROR_NOT_STARTING.with(ChatColor.RED));
      return;
    }

    Optional<Duration> duration = Optional
        .of(match.getMap().getCountdownConfig().getDuration(StartingCountdown.class));
    if (cmd.argsLength() == 1) {
      if (!sender.hasPermission("atlas.states.start.modifytime")) {
        throw new TranslatableCommandErrorException(Translations.ERROR_PERMISSION_MODTIME);
      }

      try {
        duration = Optional.of(Strings.toDuration(cmd.getString(0)));
      } catch (Exception e) {
        throw new WrappedCommandException(e);
      }
    }

    StartingCountdown countdown = new StartingCountdown(Atlas.getMatch(), duration);
    Atlas.get().getMatchManager().getRotation().startMatch(countdown);
  }

  @Command(aliases = "end", desc = "End the match.", max = 1, flags = "t:w:p:o", usage = "/end [-t time] [-w winner] [-p places] -o (Determine winner based on objectives)")
  @CommandPermissions("atlas.states.end")
  public static void end(CommandContext cmd, CommandSender sender) throws CommandException {
    Match match = Atlas.getMatch();
    if (match == null) {
      throw new CommandMatchException();
    }

    StatesModule module = match.getRequiredModule(StatesModule.class);

    if (!module.isPlaying()) {
      sender.sendMessage(Messages.ERROR_NOT_PLAYING.with(ChatColor.RED));
      return;
    }

    int places = cmd.hasFlag('p') ? cmd.getFlagInteger('p') : 1;

    Optional<Group> winner = Optional.empty();

    if (cmd.hasFlag('w') || cmd.argsLength() > 0) {
      Group group = null;

      String query = cmd.hasFlag('w') ? cmd.getFlag('w') : cmd.getJoinedStrings(0);

      List<Group> search = match.getRequiredModule(GroupsModule.class).search(sender, query);
      if (search.size() > 0) {
        group = search.get(0);
      }

      if (group == null) {
        sender.sendMessage(Messages.ERROR_TEAM_NOT_FOUND.with(ChatColor.RED));
        return;
      }

      if (!(group instanceof Team)) {
        sender.sendMessage(Messages.ERROR_TEAM_INVALID.with(ChatColor.RED));
        return;
      }

      winner = Optional.of(group);
    }

    boolean natural = !winner.isPresent() && cmd.hasFlag('o');

    Optional<Check> endTime = Optional.empty();

    if (cmd.hasFlag('t')) {
      try {
        endTime = Optional.of(new TimeCheck(
            match.getRequiredModule(StatesModule.class).getTotalPlayingDuration()
                .plus(periodFormatter.parsePeriod(cmd.getFlag('t')).toStandardDuration()),
            NumberComparator.EQUALS));
      } catch (Exception e) {
        sender.sendMessage(ChatColor.RED + "Invalid duration format.");
        return;
      }
    }

    Check end = endTime.orElse(new StaticResultCheck(CheckResult.ALLOW));

    EndScenario newEnd;

    if (natural) {
      newEnd = new ObjectivesScenario(match, end, places);
    } else if (winner.isPresent()) {
      newEnd = new TeamScenario(match, end, places, (Team) winner.get());
    } else {
      if (endTime.isPresent()) {
        sender.sendMessage(ChatColor.RED + "A time cannot be provided without -w or -o");
        return;
      }
      new TieScenario(match, end, places)
          .execute(match, match.getRequiredModule(GroupsModule.class));

      return;
    }

    ResultsModule resultsModule = match.getRequiredModule(ResultsModule.class);

    resultsModule.close();

    resultsModule.getScenarios().clear();
    resultsModule.getScenarios().add(newEnd);
    resultsModule.updateTimeBased();

    if (resultsModule.getEndingCountdown().isPresent()) {
      CompendiumPlugin.getInstance().getCountdownManager()
          .start(resultsModule.getEndingCountdown().get());
    }
  }
}