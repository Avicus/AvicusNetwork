package net.avicus.atlas.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import java.util.Optional;
import java.util.UUID;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.map.AtlasMap;
import net.avicus.atlas.map.library.MapLibrary;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.Paste;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.UnlocalizedText;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DevCommands {

  @Command(aliases = "atlas", desc = "Atlas version.")
  @CommandPermissions("atlas.dev.version")
  public static void atlas(CommandContext cmd, final CommandSender sender) {
    String version = Atlas.get().getDescription().getVersion();
    sender.sendMessage(Messages.GENERIC_VERSION.with(ChatColor.GOLD, version));
  }

  @Command(aliases = {"reloadlibrary", "refreshlibrary"}, desc = "Reload the map libraries.")
  @CommandPermissions("atlas.dev.reloadlibrary")
  public static void reloadlibrary(CommandContext cmd, final CommandSender sender) {
    for (MapLibrary library : Atlas.get().getMapManager().getLibraries()) {
      library.build();
    }
    sender.sendMessage(Messages.GENERIC_LIBRARIES_RELOADED.with(ChatColor.GOLD));
  }

  @Command(aliases = "testall", desc = "Try to parse every map in the loaded libraries.")
  @CommandPermissions("atlas.dev.testall")
  public static void testAll(CommandContext cmd, final CommandSender sender) {
    sender.sendMessage(
        "Beginning Parsing (This may take a while) \n Keep in mind that only errors will be displayed.");
    parseAll(sender);
  }

  @Command(aliases = "dump", desc = "Dump Atlas variables to a paste.")
  @CommandPermissions("atlas.dev.dump")
  public static void dump(CommandContext cmd, CommandSender sender) {
    String title = String.format("Atlas Dump %s", UUID.randomUUID().toString().substring(0, 6));
    String text = "Match: \n" +
        Atlas.getMatch() +
        "\n\n\n" +
        "Players: \n" +
        Bukkit.getOnlinePlayers() +
        "\n\n\n" +
        "Map libraries: \n" +
        Atlas.get().getMapManager().getLibraries();

    String url = new Paste(title, "*Console", text, true).upload();

    sender.sendMessage(Messages.GENERIC_DUMP.with(ChatColor.GOLD, url));
  }

  @Command(aliases = {"queuerestart", "qr"}, desc = "Queue a restart after the match.")
  @CommandPermissions("atlas.dev.queuerestart")
  public static void queueRestart(CommandContext cmd, CommandSender sender)
      throws CommandPermissionsException {
    Atlas.get().getMatchManager().getRotation().queueRestart();
    sender.sendMessage("Restart queued.");
  }

  @Command(aliases = "check", desc = "Run a check.", usage = "<check-id>")
  @CommandPermissions("atlas.dev.check")
  public static void check(CommandContext cmd, CommandSender sender) throws CommandException {
    Match match = Atlas.getMatch();
    Check check = match.getRegistry().get(Check.class, cmd.getJoinedStrings(0), true).orElse(null);
    if (check == null) {
      throw new CommandException("Check not found.");
    }
    CheckContext context = new CheckContext(match);
    if (sender instanceof Player) {
      context.add(new PlayerVariable((Player) sender));
    }

    CheckResult result = check.test(context);
    switch (result) {
      case ALLOW:
        sender.sendMessage(ChatColor.GREEN + "ALLOW");
        return;
      case DENY:
        sender.sendMessage(ChatColor.RED + "DENY");
        return;
      case IGNORE:
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "IGNORE");
    }
  }

  private static void parseAll(CommandSender sender) {
    int success = 0;
    int tried = 0;
    for (MapLibrary library : Atlas.get().getMapManager().getLibraries()) {
      for (AtlasMap source : library.getMaps()) {
        tried++;
        Optional<Match> match = parse(sender, source);
        if (match.isPresent()) {
          success++;
        }
      }
    }
    sender.sendMessage(
        "PARSING FINISHED: \n" + "(" + success + "/" + tried + ") maps parsed successfully.");
  }

  private static Optional<Match> parse(CommandSender sender, AtlasMap map) {
    try {
      return Optional.of(Atlas.get().getMatchManager().getFactory().create(map));
    } catch (Exception e) {
      String folder = map.getName();
      sender.sendMessage(Messages.ERROR_PARSING_FAILED.with(ChatColor.RED, folder));

      Localizable error = new UnlocalizedText(e.getMessage());

      error.style().color(ChatColor.RED);

      sender.sendMessage(error);
      return Optional.empty();
    }
  }

}
