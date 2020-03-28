package net.avicus.atlas.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.command.exception.CommandMatchException;
import net.avicus.atlas.event.group.GroupMaxPlayerCountChangeEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.Spectators;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GroupCommands {

  @Command(aliases = "rename", desc = "Rename a group. Provide a blank name to reset", usage = "<old>:<new>", min = 1, max = -1)
  @CommandPermissions("atlas.groups.rename")
  public static void rename(CommandContext cmd, CommandSender sender) throws CommandException {
    String[] args = cmd.getJoinedStrings(0).split(":");

    Match match = Atlas.getMatch();

    if (match == null) {
      throw new CommandMatchException();
    }

    GroupsModule groups = match.getRequiredModule(GroupsModule.class);

    List<Group> search = groups.search(sender, args[0]);

    if (search.isEmpty()) {
      sender.sendMessage(Messages.ERROR_TEAM_NOT_FOUND.with(org.bukkit.ChatColor.RED));
      return;
    }

    Group target = search.get(0);

    // Reset
    if (args.length == 1) {
      boolean success = groups.renameGroup(target, target.getOriginalName());

      if (success) {
        sender.sendMessage(Messages.GENERIC_GROUP_RENAMED.with(ChatColor.GREEN));
      } else {
        sender.sendMessage(Messages.ERROR_CANNOT_RENAME.with(ChatColor.RED));
      }
      return;
    }

    boolean success = groups.renameGroup(target, new LocalizedXmlString(args[1]));
    if (success) {
      sender.sendMessage(Messages.GENERIC_GROUP_RENAMED.with(ChatColor.GREEN));
    } else {
      sender.sendMessage(Messages.ERROR_CANNOT_RENAME.with(ChatColor.RED));
    }
  }

  @Command(aliases = "max", desc = "Set the max player count", usage = "<id> <max> [overfill]", min = 2, max = 3)
  @CommandPermissions("atlas.groups.max")
  public static void max(CommandContext cmd, CommandSender sender) throws CommandException {
    final Match match = Atlas.getMatch();
    if (match == null) {
      throw new CommandMatchException();
    }

    final GroupsModule groups = match.getRequiredModule(GroupsModule.class);
    final List<Group> search = groups.search(sender, cmd.getString(0));
    if (search.isEmpty()) {
      sender.sendMessage(Messages.ERROR_TEAM_NOT_FOUND.with(org.bukkit.ChatColor.RED));
      return;
    }

    for (Group group : search) {
      if (group instanceof Spectators) {
        continue;
      }
      group.setMaxPlayers(cmd.getInteger(1), cmd.getInteger(2, -1));
      sender.sendMessage(Translations.COMMANDS_GROUP_MAX_SUCCESS.with(ChatColor.GREEN,
          new LocalizedNumber(group.getMaxPlayers(), TextStyle.ofColor(ChatColor.YELLOW)),
          new LocalizedNumber(group.getMaxOverfill(), TextStyle.ofColor(ChatColor.YELLOW)),
          group.getName().toText(group.getChatColor())));
      Events.call(new GroupMaxPlayerCountChangeEvent(group));
    }
  }

  @Command(aliases = "force", desc = "Force a player onto a team", usage = "<player> <id>", min = 2, max = 2)
  @CommandPermissions("atlas.groups.force")
  public static void force(CommandContext cmd, CommandSender sender) throws CommandException {
    final Match match = Atlas.getMatch();
    if (match == null) {
      throw new CommandMatchException();
    }

    @Nullable Player target = Bukkit.getPlayer(cmd.getString(0));
    if (target == null) {
      sender.sendMessage(MagmaTranslations.ERROR_UNKNOWN_PLAYER
          .with(ChatColor.RED, new UnlocalizedText(cmd.getString(0))));
      return;
    }

    final GroupsModule groups = match.getRequiredModule(GroupsModule.class);
    final List<Group> search = groups.search(sender, cmd.getString(1));
    if (search.isEmpty()) {
      sender.sendMessage(Messages.ERROR_TEAM_NOT_FOUND.with(org.bukkit.ChatColor.RED));
      return;
    }

    groups.changeGroup(target,
        Optional.of(groups.getGroup(target)),
        search.get(0),
        true,
        true,
        true,
        true
    );
  }

  public static class GroupParentCommand {

    @Command(aliases = "group", usage = "<rename>", desc = "Manage groups.", min = 1)
    @NestedCommand(GroupCommands.class)
    public static void team(CommandContext cmd, CommandSender sender) {
      // Never called
    }
  }
}
