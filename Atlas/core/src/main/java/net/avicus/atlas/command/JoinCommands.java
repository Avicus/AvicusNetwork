package net.avicus.atlas.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.AtlasConfig;
import net.avicus.atlas.command.exception.CommandMatchException;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.Spectators;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import net.avicus.compendium.locale.text.LocalizedText;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommands {

  public static final String PICK_PERMISSION = "atlas.join.pick";
  public static final String FULL_PERMISSION = "atlas.join.full";
  public static final String FORCE_PERMISSION = "atlas.join.force";

  @Command(aliases = {"join", "j"}, desc = "Join a team.", usage = "<team>", min = 0, max = -1)
  public static void join(CommandContext cmd, CommandSender sender) throws CommandException {
    MustBePlayerCommandException.ensurePlayer(sender);

    Player player = (Player) sender;

    Match match = Atlas.getMatch();

    if (match == null) {
      throw new CommandMatchException();
    }

    GroupsModule groups = match.getRequiredModule(GroupsModule.class);
    Group from = groups.getGroup(player);
    Group to;

    if (cmd.argsLength() == 0) {
      List<Group> minTeams = new ArrayList<>();

      for (Group test : groups.getGroups()) {
        if (test.isSpectator()) {
          continue;
        }
        if (test.equals(from)) {
          continue;
        }

        if (minTeams.size() == 0 || test.filledPortion() < minTeams.get(0).filledPortion()) {
          minTeams.clear();
          minTeams.add(test);
        } else if (test.filledPortion() == minTeams.get(0).filledPortion()) {
          minTeams.add(test);
        }
      }

      if (minTeams.size() > 0) {
        Collections.shuffle(minTeams);
        to = minTeams.get(0);
      } else {
        to = null;
      }
    } else {
      List<Group> search = groups.search(sender, cmd.getJoinedStrings(0));
      if (search.size() > 0) {
        to = search.get(0);
      } else {
        to = null;
      }

      if (to != null && !to.isSpectator() && !player.hasPermission(PICK_PERMISSION) && !AtlasConfig
          .isScrimmage()) {
        sender.sendMessage(Messages.ERROR_CANNOT_PICK_TEAM.with(ChatColor.RED));
        return;
      }
    }

    if (to == null) {
      sender.sendMessage(Messages.ERROR_TEAM_NOT_FOUND.with(ChatColor.RED));
      return;
    }

    if (from.equals(to)) {
      LocalizedText message;
      if (from.isSpectator()) {
        message = Messages.ERROR_ALREADY_SPECTATOR.with();
      } else {
        message = Messages.ERROR_ALREADY_TEAM.with(to.getName().toText());
      }
      message.style().color(ChatColor.RED);
      sender.sendMessage(message);
      return;
    }

    boolean toPlaying = !to.isObserving();
    boolean canSwitchBeforeStart = to.isObserving() && player.hasPermission(PICK_PERMISSION);

    if (!from.isSpectator() && !canSwitchBeforeStart) {
      sender.sendMessage(Messages.ERROR_ALREADY_PLAYING.with(ChatColor.RED));
      return;
    }

    StatesModule states = match.getRequiredModule(StatesModule.class);
    if (states.isCycling() && !to.isSpectator()) {
      sender.sendMessage(Messages.ERROR_CANNOT_JOIN_CYCLING.with(ChatColor.RED));
      return;
    }

    // Check full teams
    if (to.isFull(false)) {
      if (!player.hasPermission(FULL_PERMISSION)) {
        sender.sendMessage(Messages.ERROR_CANNOT_JOIN_FULL.with(ChatColor.RED));
        return;
      }

      if (to.isFull(true)) {
        if (!player.hasPermission(FORCE_PERMISSION)) {
          sender.sendMessage(Messages.ERROR_CANNOT_JOIN_OVERFILL.with(ChatColor.RED));
          return;
        }
      }
    }

    // Check if balanced with one additional player
    if (!groups.isGroupBalanced(to, 1) && !AtlasConfig.isScrimmage()) {
      if (!player.hasPermission(FORCE_PERMISSION)) {
        sender.sendMessage(Messages.ERROR_CANNOT_JOIN_IMBALANCE.with(ChatColor.RED));
        return;
      }
    }

    boolean triggerSpawn = toPlaying || to.isSpectator();

    Optional<Group> joined = groups
        .changeGroup(player, Optional.of(from), to, triggerSpawn, toPlaying);
    if (joined.isPresent()) {
      sender.sendMessage(
          Messages.GENERIC_JOINED
              .with(joined.get().getName().toText(joined.get().getChatColor())));
    }
  }

  @Command(aliases = {"leave", "l"}, desc = "Leave your team.", max = 0)
  public static void leave(CommandContext cmd, CommandSender sender) throws CommandMatchException {
    Match match = Atlas.getMatch();

    if (match == null) {
      throw new CommandMatchException();
    }

    GroupsModule module = match.getRequiredModule(GroupsModule.class);
    Spectators spectators = module.getSpectators();
    module.changeGroup((Player) sender, spectators, spectators.isSpectator(), false);
    sender.sendMessage(
        Messages.GENERIC_JOINED.with(spectators.getName().toText(spectators.getChatColor())));
  }
}
