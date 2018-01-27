package net.avicus.atlas.module.vote;

import com.google.common.base.Splitter;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.command.exception.CommandMatchException;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.StringUtil;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import net.avicus.compendium.commands.exception.TranslatableCommandErrorException;
import net.avicus.compendium.locale.text.UnlocalizedText;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class VoteCommands {

  private static final Splitter SPLITTER = Splitter.on(" ");

  private static VoteModule module() throws CommandException {
    @Nullable final Match match = Atlas.getMatch();
    if (match == null) {
      throw new CommandMatchException();
    }

    @Nullable final VoteModule module = match.getModule(VoteModule.class).orElse(null);
    if (module == null) {
      throw new TranslatableCommandErrorException(Messages.VOTE_DISABLED);
    }

    return module;
  }

  @Command(aliases = {"vote"}, desc = "Vote for a map", min = 1, max = 1, usage = "<map>")
  public static void vote(final CommandContext args, final CommandSender source)
      throws CommandException {
    MustBePlayerCommandException.ensurePlayer(source);
    final VoteModule module = module();
    final Match map = module.getOptional(args.getInteger(0));
    if (map == null) {
      throw new TranslatableCommandErrorException(Messages.VOTE_INVALID);
    }

    module.cast((Player) source, map);
    source.sendMessage(Messages.VOTE_SUCCESS
        .with(ChatColor.GREEN, new UnlocalizedText(map.getMap().getName(), ChatColor.DARK_AQUA)));
  }

  @Command(aliases = "votes", usage = "<start>", desc = "Manage voting", min = 1)
  @NestedCommand(Votes.class)
  public static void votes(final CommandContext args, final CommandSender source) {
  }

  public static class Votes {

    @Command(aliases = {
        "start"}, desc = "Start a vote for the next map", min = 1, usage = "<map...>", flags = "t:c")
    @CommandPermissions("atlas.vote.start.map")
    public static void start(final CommandContext args, final CommandSender source)
        throws CommandException {
      final VoteModule module = module();
      module.start(VoteModule.parse(SPLITTER.splitToList(args.getJoinedStrings(0))),
          args.hasFlag('t') ? args.getFlag('t') : null, args.hasFlag('c'));
      source.sendMessage(Messages.VOTE_SET.with(ChatColor.GRAY, new UnlocalizedText(StringUtil
          .listToEnglishCompound(
              module.getOptions().values().stream().map(match -> match.getMap().getName())
                  .collect(Collectors.toList()), ChatColor.DARK_AQUA.toString(),
              ChatColor.GRAY.toString()))));
      if (args.hasFlag('c')) {
        source.sendMessage(Messages.VOTE_DELAY.with(ChatColor.GREEN));
      }
    }

    @Command(aliases = {"cancel"}, desc = "Cancel a vote for the next map", max = 0, flags = "b")
    @CommandPermissions("atlas.vote.cancel.map")
    public static void cancel(final CommandContext args, final CommandSender source)
        throws CommandException {
      final VoteModule module = module();
      final boolean cancelled = module.cancel();
      if (cancelled) {
        if (args.hasFlag('b')) {
          module.match.broadcast(Messages.VOTE_CANCELLED.with(ChatColor.GREEN));
        } else {
          source.sendMessage(Messages.VOTE_CANCELLED.with(ChatColor.GREEN));
        }
      } else {
        source.sendMessage(Messages.VOTE_CANCELNONE.with(ChatColor.RED));
      }
    }
  }
}
