package net.avicus.mars.managed;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.NestedCommand;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.User;
import net.avicus.mars.CompetitiveEvent;
import net.avicus.mars.MarsPlugin;
import net.avicus.mars.MarsTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ManagedCommands {

  @Command(aliases = {
      "invite"}, desc = "Toggle a team or player invite to a managed event.", min = 1, max = -1, flags = "p", usage = "<team/player>")
  public static void invite(CommandContext cmd, CommandSender sender)
      throws CommandPermissionsException {
    CompetitiveEvent event = MarsPlugin.getInstance().getEvents().getCurrentEvent().orElse(null);

    if (!(event instanceof ManagedMatch)) {
      sender.sendMessage(ChatColor.RED + "There is no managed match currently active.");
      return;
    }

    ManagedMatch match = (ManagedMatch) event;

    if (!(match.canInvite(sender))) {
      sender.sendMessage(
          ChatColor.RED + "You are unable to invite to this match other players and teams.");
      return;
    }

    String query = cmd.getJoinedStrings(0);
    boolean isPlayer = cmd.hasFlag('p');

    if (isPlayer) {
      Bukkit.getServer().getScheduler().runTaskAsynchronously(MarsPlugin.getInstance(), () -> {
        User user = Magma.get().database().getUsers().findByName(query).orElse(null);

        if (user == null) {
          sender.sendMessage(ChatColor.RED + "No player matched query.");
          return;
        }

        boolean invited = match.toggleInviteUser(user);
        if (invited) {
          sender.sendMessage(
              ChatColor.YELLOW + "You have invited " + user.getName() + " to this scrimmage.");
        } else {
          sender.sendMessage(
              ChatColor.YELLOW + "You have uninvited " + user.getName() + " to this scrimmage.");
        }
      });
    } else {
      Bukkit.getServer().getScheduler().runTaskAsynchronously(MarsPlugin.getInstance(), () -> {
        MarsTeam team = MarsPlugin.getInstance().createTeam(query).orElse(null);

        if (team == null) {
          sender.sendMessage(ChatColor.RED + "No team matched query.");
          return;
        }

        boolean invited = match.toggleInviteTeam(team);

        if (invited) {
          sender.sendMessage(
              ChatColor.YELLOW + "You have invited " + team.getName() + " to this scrimmage.");
        } else {
          sender.sendMessage(
              ChatColor.YELLOW + "You have uninvited " + team.getName() + " to this scrimmage.");
        }
      });
    }
  }

  public static class ManagedParentCommand {

    @Command(aliases = {"events", "e"}, usage = "<invite>", desc = "Manage events.", min = 1)
    @NestedCommand(ManagedCommands.class)
    public static void events(CommandContext cmd, CommandSender sender) {
      // Never called
    }
  }
}
