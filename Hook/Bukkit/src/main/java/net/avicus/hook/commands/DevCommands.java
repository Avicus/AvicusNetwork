package net.avicus.hook.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import net.avicus.hook.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DevCommands {

  @Command(aliases = {
      "permissions"}, desc = "View permissions for a player and which plugin assigned them.", max = 1)
  public static void permissions(CommandContext cmd, CommandSender sender) {
    String query = cmd.getString(0);
    Player search = Bukkit.getPlayer(query);

    if (search == null) {
      sender.sendMessage(Messages.ERROR_NO_PLAYERS.with(ChatColor.RED));
      return;
    }

    sender.sendMessage(ChatColor.GOLD + "Permissions for " + search.getDisplayName());
    search.getEffectivePermissions().forEach(attachment -> {
      if (attachment.getAttachment() == null) {
        return;
      }

      String assigner = "Bukkit";
      if (attachment.getAttachment().getPlugin() != null) {
        assigner = attachment.getAttachment().getPlugin().getName();
      }
      String permission = attachment.getPermission();
      ChatColor color = attachment.getValue() ? ChatColor.GREEN : ChatColor.RED;

      sender.sendMessage(ChatColor.AQUA + assigner + " - " + color + permission);
    });
  }

  @Command(aliases = {"has-permission",
      "hp"}, desc = "Check if a player has a permission.", max = 2, usage = "<player> <permission>")
  public static void hasPerm(CommandContext cmd, CommandSender sender) {
    String query = cmd.getString(0);
    Player search = Bukkit.getPlayer(query);

    if (search == null) {
      sender.sendMessage(Messages.ERROR_NO_PLAYERS.with(ChatColor.RED));
      return;
    }

    sender.sendMessage(
        search.hasPermission(cmd.getString(1)) ? ChatColor.GREEN + "YES" : ChatColor.RED + "NO");
  }

  public static class Parent {

    @CommandPermissions("hook.dev")
    @Command(aliases = {"dev"}, desc = "Development commands")
    @NestedCommand(DevCommands.class)
    public static void parent(CommandContext args, CommandSender source) {
    }
  }
}
