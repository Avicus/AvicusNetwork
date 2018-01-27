package net.avicus.mars.scrimmage;

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

public class ScrimmageCommands {

  @Command(aliases = {
      "invite"}, desc = "Toggle a team or player invite to a scrimmage.", min = 1, max = -1, flags = "p", usage = "<team/player>")
  public static void invite(CommandContext cmd, CommandSender sender)
      throws CommandPermissionsException {
    CompetitiveEvent event = MarsPlugin.getInstance().getEvents().getCurrentEvent().orElse(null);

    if (!(event instanceof Scrimmage)) {
      sender.sendMessage(ChatColor.RED + "There is no scrimmage currently active.");
      return;
    }

    Scrimmage scrim = (Scrimmage) event;

    if (!(scrim.canInvite(sender))) {
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

        boolean invited = scrim.toggleInviteUser(user);
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

        boolean invited = scrim.toggleInviteTeam(team);

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

  public static class ScrimmageParentCommand {

    @Command(aliases = {"scrimmage",
        "scrim"}, usage = "<invite>", desc = "Manage scrimmages.", min = 1)
    @NestedCommand(ScrimmageCommands.class)
    public static void scrimmage(CommandContext cmd, CommandSender sender) {
      // Never called
    }
  }
}
