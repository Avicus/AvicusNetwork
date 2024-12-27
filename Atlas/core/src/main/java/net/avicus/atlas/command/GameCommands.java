package net.avicus.atlas.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandNumberFormatException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.command.exception.CommandMatchException;
import net.avicus.atlas.map.AtlasMap;
import net.avicus.atlas.map.library.MapLibrary;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.module.stats.StatsModule;
import net.avicus.atlas.util.Messages;
import net.avicus.atlas.util.ObjectiveRenderer;
import net.avicus.atlas.util.ObjectiveUtils;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.Paginator;
import net.avicus.compendium.StringUtil;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.commands.exception.InvalidPaginationPageException;
import net.avicus.compendium.commands.exception.TranslatableCommandErrorException;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.MultiPartLocalizable;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.utils.Strings;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class GameCommands {
//    @Command(aliases = "match", desc = "View match information.", max = 0)
//    public static void match(CommandContext cmd, CommandSender sender) throws CommandMatchException {
//        Match match = Atlas.getMatch();
//
//        if (match == null)
//            throw new CommandMatchException();
//
//
//    }

  public static ObjectiveRenderer RENDERER = new ObjectiveRenderer() {
    @Override
    public String getDisplay(Match match, Competitor competitor, Player viewer, Objective objective,
        boolean showName) {
      return objective.getName().render(viewer);
    }
  };

  @Command(aliases = "objectives", desc = "View match objectives.", max = 0)
  public static void objectives(CommandContext cmd, CommandSender sender) throws CommandException {
    Match match = Atlas.getMatch();

    if (match == null) {
      throw new CommandMatchException();
    }

    AtlasMap map = match.getMap();

    // Objectives
    List<String> objectiveText = new ArrayList<>();

    if (match.hasModule(ObjectivesModule.class)) {
      ObjectivesModule module = match.getRequiredModule(ObjectivesModule.class);

      if (sender instanceof ConsoleCommandSender) {
        objectiveText.addAll(module.getObjectives()
            .stream()
            .filter(Objective::show)
            .map(objective -> objective.getName(sender))
            .collect(Collectors.toList()));
      } else {
        objectiveText.addAll(ObjectiveUtils.objectivesByTeam(match, (Player) sender, module,
            match.getRequiredModule(GroupsModule.class), RENDERER));
      }
    }

    if (objectiveText.isEmpty()) {
      throw new TranslatableCommandErrorException(Messages.ERROR_COMMAND_NOT_ENABLED);
    }

    // Main
    sender.sendMessage(Strings.padChatComponent(
        Messages.UI_MATCH.with(ChatColor.GOLD, map.getName()).render(sender), "-",
        ChatColor.BLUE, ChatColor.AQUA));

    // Objectives
    if (!objectiveText.isEmpty()) {
      Iterator<String> i = objectiveText.iterator();
      while (i.hasNext()) {
        String s = i.next();

        if (ChatColor.stripColor(s).trim().isEmpty()) {
          i.remove();
        }
      }

      sender.sendMessage(Messages.UI_OBJECTIVES.with(ChatColor.GREEN));
      sender.sendMessage("   " + StringUtils.join(objectiveText, "\n   "));
    }

  }

  @Command(aliases = "map", desc = "View information about the current map.", max = 0)
  public static void map(CommandContext cmd, CommandSender sender) throws CommandMatchException {
    Match match = Atlas.getMatch();

    if (match == null) {
      throw new CommandMatchException();
    }

    AtlasMap map = match.getMap();

    // Header
    BaseComponent name = map.getClickableName(sender, true);
    BaseComponent header = Strings
        .padTextComponent(name, " ", ChatColor.DARK_AQUA.toString() + ChatColor.STRIKETHROUGH,
            ChatColor.BLUE);

    // Authors
    String authorString = StringUtil.join(map.getAuthors(), "\n", author -> {
      String name1 = author.getName();

      String promoLink = "";
      if (author.getPromoLink().isPresent()) {
        promoLink =
            ChatColor.GOLD + " (" + ChatColor.BLUE + ChatColor.ITALIC + author
                .getPromoLink().get()
                .render(sender) + ChatColor.GOLD + ")";
      }

      return "   " + ChatColor.AQUA + name1 + promoLink;
    });

    String contribString = StringUtil.join(map.getContributors(), "\n", author -> {
      String name1 = author.getName();
      String role = "";
      if (author.getRole().isPresent()) {
        role = ChatColor.GOLD + " (" + ChatColor.BLUE + ChatColor.ITALIC + author.getRole()
            .get()
            + ChatColor.GOLD + ")";
      }

      String promoLink = "";
      if (author.getPromoLink().isPresent()) {
        promoLink =
            ChatColor.GOLD + " (" + ChatColor.BLUE + ChatColor.ITALIC + author
                .getPromoLink().get()
                .render(sender) + ChatColor.GOLD + ")";
      }

      return "   " + ChatColor.AQUA + name1 + role + promoLink;
    });

    // Todo: Fetch tips somehow? Maybe have a configurable list of things to display here?
//        String tipString = "";
//        if (match.hasModule(BroadcastsModule.class)) {
//            tipString = Strings.join(match.getRequiredModule(BroadcastsModule.class).getTips(), "\n", new Strings.Stringify<Broadcast>() {
//                @Override
//                public String on(Broadcast broadcast) {
//                    return "   " + broadcast.getMessage().render(sender);
//                }
//            });
//        }

    // Main
    sender.sendMessage(header);

    // Authors
    if (!authorString.isEmpty()) {
      sender.sendMessage(Messages.UI_AUTHORS.with(ChatColor.GREEN));
      sender.sendMessage(authorString);
    }

    // Contributors
    if (!contribString.isEmpty()) {
      sender.sendMessage(Messages.UI_CONTRIBUTORS.with(ChatColor.GREEN));
      sender.sendMessage(contribString);
    }

//        if (!tipString.isEmpty()) {
//            sender.sendMessage(Messages.UI_TIPS.with(ChatColor.GREEN));
//            sender.sendMessage(tipString);
//        }
  }

  @Command(aliases = "maps", desc = "View all available maps.", max = 1)
  public static void maps(CommandContext cmd, CommandSender sender)
      throws CommandNumberFormatException, InvalidPaginationPageException {
    List<AtlasMap> maps = new ArrayList<>();
    for (MapLibrary library : Atlas.get().getMapManager().getLibraries()) {
      maps.addAll(library.getMaps());
    }

    // Alphabetize
    maps.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));

    Paginator<AtlasMap> paginator = new Paginator<>(maps, 9);
    int pageNumber = cmd.getInteger(0, 1) - 1;

    Collection<AtlasMap> page;
    try {
      page = paginator.getPage(pageNumber);
    } catch (IllegalArgumentException e) {
      throw new InvalidPaginationPageException(paginator);
    }

    Localizable page1 = new LocalizedNumber(pageNumber + 1, TextStyle.ofColor(ChatColor.GREEN));
    Localizable page2 = new LocalizedNumber(paginator.getPageCount(),
        TextStyle.ofColor(ChatColor.GREEN));
    Localizable header = Messages.UI_MAPS.with(ChatColor.DARK_AQUA, page1, page2);
    sender.sendMessage(header);

    for (AtlasMap map : page) {
      Localizable part1 = new UnlocalizedText((paginator.getIndex(map) + 1) + " ",
          ChatColor.DARK_AQUA);
      Localizable part2 = new UnlocalizedText("  ", TextStyle.create().strike());
      Localizable part3 = new UnlocalizedText(" " + map.getName(), ChatColor.GREEN);

      sender.sendMessage(new MultiPartLocalizable(part1, part2, part3));
    }

  }

  @Command(aliases = "facts", desc = "View facts from this match.", max = 0)
  public static void facts(CommandContext cmd, CommandSender sender) throws CommandException {
    Match match = Atlas.getMatch();

    if (match == null) {
      throw new CommandMatchException();
    }

    if (!match.getRequiredModule(StatesModule.class).isCycling()) {
      throw new TranslatableCommandErrorException(Translations.STATS_FACTS_ERROR_AFTER);
    }

    Optional<StatsModule> module = match.getModule(StatsModule.class);
    if (!module.isPresent()) {
      throw new TranslatableCommandErrorException(Translations.STATS_FACTS_ERROR_DISABLED);
    }

    // Header
    BaseComponent name = Translations.STATS_FACTS_ALL.with(ChatColor.GOLD).render(sender);
    sender.sendMessage(Strings
        .padTextComponent(name, " ", ChatColor.DARK_AQUA.toString() + ChatColor.STRIKETHROUGH,
            ChatColor.BLUE));
    module.get().getMatchFacts().forEach(sender::sendMessage);
  }
}
