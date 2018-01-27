package net.avicus.magma.network.server;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import net.avicus.compendium.commands.exception.TranslatableCommandErrorException;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ServerCommands {

  @Command(aliases = {"lobby", "hub"}, desc = "Connect to the lobby", min = 0, max = 0)
  public static void lobby(final CommandContext context, final CommandSender sender)
      throws MustBePlayerCommandException {
    final Player player = MustBePlayerCommandException.ensurePlayer(sender);
    Magma.get().getMm().get(ServerModule.class).moveToLobby(player);
  }

  @Command(aliases = {"server",
      "sv"}, desc = "View the current server, or connect to one.", usage = "(server)", flags = "f", min = 0, max = 1)
  public static void server(CommandContext cmd, CommandSender sender)
      throws TranslatableCommandErrorException {
    if (cmd.argsLength() == 0) {
      Server server = Magma.get().localServer();
      sender.sendMessage(
          MagmaTranslations.COMMANDS_SERVER_CURRENT.with(ChatColor.YELLOW, server.getName()));
    } else {
      MustBePlayerCommandException.ensurePlayer(sender);

      String query = cmd.getString(0).toLowerCase();
      Server found = null;
      List<Server> possibilities = new ArrayList<>();
      for (Server server : Servers.getServerCache()) {
        if (server.getName().equalsIgnoreCase(query)) {
          found = server;
          break;
        } else if (server.getName().toLowerCase().startsWith(query)) {
          possibilities.add(server);
        }
      }

      // Remove possible matches that are local and sort so online is first.
      possibilities.removeIf(Server::isLocal);
      possibilities.sort(Comparator.comparing(
          server -> !Servers.getStatus(server).map(ServerStatus::isOnline).orElse(false)));

      if (found == null && possibilities.isEmpty()) {
        throw new TranslatableCommandErrorException(MagmaTranslations.COMMANDS_SERVER_QUERY_NONE);
      }

      if (found == null) {
        found = possibilities.get(0);
      }

      if (found == null) {
        throw new TranslatableCommandErrorException(MagmaTranslations.COMMANDS_SERVER_QUERY_NONE);
      }

      Servers.connect((Player) sender, found, cmd.hasFlag('f'), true);
    }
  }

  @CommandPermissions("hook.togglepermissible")
  @Command(aliases = {
      "togglepermissible"}, desc = "Toggle if the server is joinable by all, or those with permission. (Resets on restart).", max = 0)
  public static void togglepermissible(CommandContext cmd, CommandSender sender) {
    boolean enabled = Magma.get().localServer().isPermissible();

    Magma.get().localServer().setPermissible(!enabled);

    sender.sendMessage(MagmaTranslations.COMMANDS_TOGGLEPERMISSIBLE_TOGGLED
        .with(ChatColor.AQUA, !enabled ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
  }
}
