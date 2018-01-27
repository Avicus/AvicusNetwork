package net.avicus.atlas.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.command.exception.CommandMatchException;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.channels.ChannelsModule;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChannelCommands {

  @Command(aliases = {"global", "g"}, desc = "Toggle global chat.", min = 0, max = -1)
  public static void global(CommandContext cmd, final CommandSender sender)
      throws CommandException {
    Match match = Atlas.getMatch();

    MustBePlayerCommandException.ensurePlayer(sender);

    if (match == null) {
      throw new CommandMatchException();
    }

    Player player = (Player) sender;
    ChannelsModule channels = match.getRequiredModule(ChannelsModule.class);

    if (!channels.isGlobalChatAllowed()) {
      player.sendMessage(Messages.ERROR_CHAT_GLOBAL_DISABLED.with(ChatColor.RED));
      return;
    }

    if (cmd.argsLength() > 0) {
      String msg = cmd.getJoinedStrings(0);
      boolean before = channels.isGlobalEnabled(player);
      AtlasTask.of(() -> {
        channels.setGlobalEnabled(player, true);
        player.chat(msg);
        channels.setGlobalEnabled(player, before);
      }).nowAsync();
    } else {
      if (channels.isGlobalEnabled(player)) {
        Bukkit.getServer().dispatchCommand(sender, "t");
      } else {
        player.sendMessage(Messages.GENERIC_CHAT_GLOBAL_ENABLED.with(ChatColor.YELLOW));
        channels.setGlobalEnabled(player, true);
      }
    }
  }

  @Command(aliases = {"team", "t"}, desc = "Toggle global chat.", min = 0, max = -1)
  public static void team(CommandContext cmd, final CommandSender sender) throws CommandException {
    Match match = Atlas.getMatch();

    MustBePlayerCommandException.ensurePlayer(sender);

    if (match == null) {
      throw new CommandMatchException();
    }

    Player player = (Player) sender;
    ChannelsModule channels = match.getRequiredModule(ChannelsModule.class);

    if (!channels.isTeamChatAllowed()) {
      player.sendMessage(Messages.ERROR_CHAT_TEAM_DISABLED.with(ChatColor.RED));
      return;
    }

    if (cmd.argsLength() > 0) {
      String msg = cmd.getJoinedStrings(0);
      boolean before = channels.isGlobalEnabled(player);
      AtlasTask.of(() -> {
        channels.setGlobalEnabled(player, false);
        player.chat(msg);
        channels.setGlobalEnabled(player, before);
      }).nowAsync();
    } else {
      if (!channels.isGlobalEnabled(player)) {
        Bukkit.getServer().dispatchCommand(sender, "g");
      } else {
        player.sendMessage(Messages.GENERIC_CHAT_TEAM_ENABLED.with(ChatColor.YELLOW));
        channels.setGlobalEnabled(player, false);
      }
    }
  }

}
