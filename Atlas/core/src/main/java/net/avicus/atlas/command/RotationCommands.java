package net.avicus.atlas.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandNumberFormatException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.command.exception.CommandMatchException;
import net.avicus.atlas.map.AtlasMap;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.Paginator;
import net.avicus.compendium.Paste;
import net.avicus.compendium.StringUtil;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.commands.exception.InvalidPaginationPageException;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.MultiPartLocalizable;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.channel.staff.StaffChannels;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class RotationCommands {

  @Command(aliases = {"rot",
      "rotation"}, desc = "Set the current rotation.", usage = "<page>", min = 0, max = 1)
  public static void rotation(CommandContext cmd, CommandSender sender) throws CommandException {
    Match current = Atlas.getMatch();
    if (current == null) {
      throw new CommandMatchException();
    }

    Paginator<Match> paginator = new Paginator<>(
        Atlas.get().getMatchManager().getRotation().getMatches(), 5);
    int page = cmd.getInteger(0, paginator.getPageIndex(current) + 1) - 1;

    Collection<Match> list;
    try {
      list = paginator.getPage(page);
    } catch (IllegalArgumentException e) {
      throw new InvalidPaginationPageException(paginator);
    }

    Localizable page1 = new UnlocalizedText((page + 1) + "", ChatColor.GREEN);
    Localizable page2 = new UnlocalizedText(paginator.getPageCount() + "", ChatColor.GREEN);

    Localizable header = Messages.UI_ROTATION.with(ChatColor.DARK_AQUA, page1, page2);

    sender.sendMessage(header);

    for (Match match : list) {
      Localizable part1 = new UnlocalizedText((paginator.getIndex(match) + 1) + " ",
          ChatColor.DARK_AQUA);
      Localizable part2 = new UnlocalizedText("  ", TextStyle.create().strike());
      Localizable part3 = new UnlocalizedText(" " + match.getMap().getName(), ChatColor.GREEN);
      if (current.equals(match)) {
        part3.style().bold();
      }

      sender.sendMessage(new MultiPartLocalizable(part1, part2, part3));
    }
  }

  @Command(aliases = {"setnext",
      "sn"}, flags = "f", desc = "Set the next map.", usage = "<map>", min = 1)
  @CommandPermissions("atlas.rotation.setnext")
  public static void setnext(CommandContext cmd, CommandSender sender) {
    Optional<AtlasMap> map = Atlas.get().getMapManager().search(cmd.getJoinedStrings(0));

    if (!map.isPresent()) {
      sender.sendMessage(Messages.ERROR_MAP_NOT_FOUND.with(ChatColor.RED, cmd.getJoinedStrings(0)));
      return;
    }

    Optional<Match> match = parse(sender, map.get());
    if (!match.isPresent()) {
      return;
    }

    try {
      boolean result = Atlas.get().getMatchManager().getRotation()
          .next(match.get(), cmd.hasFlag('f'));
      if (result) {
        sender.sendMessage(
            Messages.GENERIC_ROT_NEXT.with(ChatColor.GOLD, match.get().getMap().getName()));
        StaffChannels.MAPDEV_CHANNEL.simpleLocalSend(null,
            new TextComponent(sender.getName() + " set the next map to " + map.get().getName()));
      } else {
        sender.sendMessage(Messages.ERROR_ROT_FAILED.with(ChatColor.RED));
      }
    } catch (IllegalArgumentException e) {
      // shouldn't happen
      e.printStackTrace();
    }
  }

  @Command(aliases = {
      "insert"}, flags = "f", desc = "Insert a map at a slot.", usage = "<slot> <map>", min = 2)
  @CommandPermissions("atlas.rotation.insert")
  public static void insert(CommandContext cmd, CommandSender sender) throws CommandException {
    int slot = cmd.getInteger(0);
    Optional<AtlasMap> map = Atlas.get().getMapManager().search(cmd.getJoinedStrings(1));

    if (!map.isPresent()) {
      sender.sendMessage(Messages.ERROR_MAP_NOT_FOUND.with(ChatColor.RED, cmd.getJoinedStrings(1)));
      return;
    }

    Optional<Match> match = parse(sender, map.get());
    if (!match.isPresent()) {
      return;
    }

    try {
      boolean result = Atlas.get().getMatchManager().getRotation()
          .insert(slot, match.get(), cmd.hasFlag('f'));
      if (result) {
        sender.sendMessage(Messages.GENERIC_ROT_INSERT
            .with(ChatColor.GOLD, match.get().getMap().getName(), slot + ""));
      } else {
        sender.sendMessage(Messages.ERROR_ROT_FAILED.with(ChatColor.RED));
      }
    } catch (IllegalArgumentException e) {
      sender.sendMessage(Messages.ERROR_BAD_SLOT.with(ChatColor.RED));
    }
  }

  @Command(aliases = {"append",
      "add"}, flags = "f", desc = "Append a map at the rotation.", usage = "<map>", min = 1)
  @CommandPermissions("atlas.rotation.append")
  public static void append(CommandContext cmd, CommandSender sender) throws CommandException {
    Optional<AtlasMap> map = Atlas.get().getMapManager().search(cmd.getJoinedStrings(0));

    if (!map.isPresent()) {
      sender.sendMessage(Messages.ERROR_MAP_NOT_FOUND.with(ChatColor.RED, cmd.getJoinedStrings(0)));
      return;
    }

    Optional<Match> match = parse(sender, map.get());
    if (!match.isPresent()) {
      return;
    }

    try {
      boolean result = Atlas.get().getMatchManager().getRotation()
          .append(match.get(), cmd.hasFlag('f'));
      if (result) {
        sender.sendMessage(
            Messages.GENERIC_ROT_APPEND.with(ChatColor.GOLD, match.get().getMap().getName()));
      } else {
        sender.sendMessage(Messages.ERROR_ROT_FAILED.with(ChatColor.RED));
      }
    } catch (IllegalArgumentException e) {
      sender.sendMessage(Messages.ERROR_BAD_SLOT.with(ChatColor.RED));
    }
  }

  @Command(aliases = {
      "set"}, flags = "f", desc = "Set the current rotation.", usage = "<maps...>", min = 1)
  @CommandPermissions("atlas.rotation.set")
  public static void set(CommandContext cmd, CommandSender sender) throws CommandException {
    List<Match> matches = new ArrayList<>();
    final String[] split = cmd.getJoinedStrings(0).split(",");
    for (int i = 0; i < split.length; i++) {
      String name = split[i].trim();

      Optional<AtlasMap> map = Atlas.get().getMapManager().search(name);

      if (!map.isPresent()) {
        sender.sendMessage(Messages.ERROR_MAP_NOT_FOUND.with(ChatColor.RED, name));
        return;
      }

      Optional<Match> match = parse(sender, map.get());
      if (match.isPresent()) {
        matches.add(match.get());
      }
    }

    int index = Atlas.get().getMatchManager().getRotation().getCurrentIndex().intValue() + 1;
    int count = Atlas.get().getMatchManager().getRotation().getNextMatches().size();

    for (int i = 0; i < count; i++) {
      try {
        boolean result = Atlas.get().getMatchManager().getRotation()
            .remove(index, cmd.hasFlag('f'));
        if (!result) {
          sender.sendMessage(Messages.ERROR_ROT_FAILED.with(ChatColor.RED));
          return;
        }
      } catch (IllegalArgumentException e) {
        sender.sendMessage(Messages.ERROR_BAD_SLOT.with(ChatColor.RED));
        return;
      }
    }

    for (Match match : matches) {
      try {
        boolean result = Atlas.get().getMatchManager().getRotation()
            .append(match, cmd.hasFlag('f'));
        if (!result) {
          sender.sendMessage(Messages.ERROR_ROT_FAILED.with(ChatColor.RED));
          return;
        }
      } catch (IllegalArgumentException e) {
        sender.sendMessage(Messages.ERROR_BAD_SLOT.with(ChatColor.RED));
        e.printStackTrace();
        return;
      }
    }

    String maps = StringUtil.join(matches, ", ", match -> match.getMap().getName());
    sender.sendMessage(Messages.GENERIC_ROT_SET.with(ChatColor.GOLD, maps));
  }

  @Command(aliases = "next", desc = "Get the next map in the rotation.", max = 1)
  public static void next(CommandContext cmd, CommandSender sender)
      throws CommandNumberFormatException {
    Optional<Match> match = Atlas.get().getMatchManager().getRotation().getNextMatch();
    if (match.isPresent()) {
      AtlasMap module = match.get().getMap();
      Localizable name = new UnlocalizedText(module.getName());
      name.style().color(ChatColor.GREEN);
      List<String> authorNames = module.getAuthors().stream()
          .map(author -> ChatColor.GOLD + author.getName() + ChatColor.AQUA)
          .collect(Collectors.toList());
      String authorString = StringUtil.listToEnglishCompound(authorNames);
      UnlocalizedText version = new UnlocalizedText("(" + module.getVersion().toString() + ")");
      version.style().color(ChatColor.DARK_PURPLE);
      sender.sendMessage(Messages.GENERIC_NEXT_MAP
          .with(ChatColor.AQUA, name, version, new UnlocalizedText(authorString)));
    } else {
      sender.sendMessage(Messages.ERROR_NO_MAPS.with(ChatColor.RED));
    }
  }

  private static Optional<Match> parse(CommandSender sender, AtlasMap map) {
    try {
      return Optional.of(Atlas.get().getMatchManager().getFactory().create(map));
    } catch (Exception e) {
      String folder = map.getName();
      sender.sendMessage(Messages.ERROR_PARSING_FAILED.with(ChatColor.RED, folder));

      new AtlasTask() {
        @Override
        public void run() {
          String text = ExceptionUtils.getFullStackTrace(e);
          String paste = new Paste("Parsing Failure: " + folder, "~Console", text, true).upload();

          UnlocalizedFormat format = new UnlocalizedFormat("{0}: {1}");
          Localizable link = new UnlocalizedText(paste == null ? "PASTE FAILED" : paste);
          link.style().color(ChatColor.DARK_RED)
              .click(new ClickEvent(ClickEvent.Action.OPEN_URL, paste));

          if (e.getMessage() == null || e.getMessage().isEmpty()) {
            sender.sendMessage(format
                .with(new UnlocalizedText("Unknown error", TextStyle.ofColor(ChatColor.RED)),
                    link));
          } else {
            sender.sendMessage(format
                .with(new UnlocalizedText(e.getMessage(), TextStyle.ofColor(ChatColor.RED)), link));
          }

          if (sender instanceof ConsoleCommandSender) {
            e.printStackTrace();
          }

        }
      }.nowAsync();
      return Optional.empty();
    }
  }
}
