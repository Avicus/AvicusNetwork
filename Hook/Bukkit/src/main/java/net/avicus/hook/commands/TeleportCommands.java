package net.avicus.hook.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommands {

  @CommandPermissions("hook.tp")
  @Command(aliases = {"tp",
      "teleport"}, desc = "Teleport to a player or location.", usage = "<player> OR <x> <y> <z>", min = 0, max = 3)
  public static void tp(CommandContext cmd, CommandSender sender) throws CommandException {
    MustBePlayerCommandException.ensurePlayer(sender);

    if (cmd.argsLength() != 1 && cmd.argsLength() != 3) {
      throw new CommandUsageException("Invalid arguments", "/tp <player> OR <x> <y> <z>");
    }

    Player player = (Player) sender;

    if (cmd.argsLength() == 1) {
      String query = cmd.getString(0);
      Player search = Bukkit.getPlayer(query);

      if (search == null) {
        sender.sendMessage(Messages.ERROR_NO_PLAYERS.with(ChatColor.RED));
        return;
      }

      User user = Users.user(search);
      player.sendMessage(
          Messages.GENERIC_TELEPORTED.with(ChatColor.GRAY, Users.getLocalizedDisplay(user)));
      player.teleport(search);
    } else {
      if (!player.hasPermission("hook.tp.location")) {
        throw new CommandPermissionsException();
      }

      int x = cmd.getInteger(0);
      int y = cmd.getInteger(1);
      int z = cmd.getInteger(2);
      float yaw = player.getLocation().getYaw();
      float pitch = player.getLocation().getPitch();

      LocalizableFormat format = new UnlocalizedFormat("{0}, {1}, {2}");
      Localizable text = format
          .with(new LocalizedNumber(x), new LocalizedNumber(y), new LocalizedNumber(z));

      player.sendMessage(Messages.GENERIC_TELEPORTED.with(ChatColor.GRAY, text));
      player.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));
    }
  }
}
