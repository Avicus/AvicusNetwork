package net.avicus.magma.announce;

import com.sk89q.minecraft.util.commands.ChatColor;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.NestedCommand;
import net.avicus.magma.Magma;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class AnnounceCommands {

  @Command(aliases = {
      "join"}, desc = "Announce to the network that users should join this server.", min = 1, usage = "<message>")
  public static void join(CommandContext cmd, CommandSender sender) throws CommandException {
    if (Magma.get().localServer().isPermissible()) {
      sender.sendMessage(
          ChatColor.RED + "You must disable permissible status for this server first.");
      return;
    }

    send(cmd.getJoinedStrings(0), AnnounceMessageHandler.AnnounceType.JOIN);
    sender.sendMessage("Message sent!");
  }

  @Command(aliases = {
      "message"}, desc = "Announce a message to the network.", min = 1, usage = "<message>")
  public static void message(CommandContext cmd, CommandSender sender) throws CommandException {
    send(cmd.getJoinedStrings(0), AnnounceMessageHandler.AnnounceType.MESSAGE);
    sender.sendMessage("Message sent!");
  }

  @Command(aliases = {
      "critical"}, desc = "Announce a critical message to the network.", min = 1, usage = "<message>")
  public static void critical(CommandContext cmd, CommandSender sender) throws CommandException {
    if (!(sender.isOp() || sender instanceof ConsoleCommandSender)) {
      throw new CommandPermissionsException();
    }
    send(cmd.getJoinedStrings(0), AnnounceMessageHandler.AnnounceType.CRITICAL);
    sender.sendMessage("Message sent!");
  }

  private static void send(String message, AnnounceMessageHandler.AnnounceType type) {
    Magma.get().getRedis().publish(new AnnounceMessageHandler.AnnounceMessage(new BaseComponent[]{
        new TextComponent(ChatColor.translateAlternateColorCodes('&', message))}, type,
        Magma.get().localServer()));
  }

  public static class Parent {

    @CommandPermissions("hook.announce")
    @Command(aliases = {"announce"}, desc = "announcement commands")
    @NestedCommand(AnnounceCommands.class)
    public static void parent(CommandContext args, CommandSender source) {
    }
  }
}
