package net.avicus.hook.sessions;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import java.util.Optional;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.hook.Hook;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.database.model.impl.User;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class FindCommand {

  @Command(aliases = {"where", "seen",
      "find"}, desc = "Locate a user.", usage = "<player>", min = 1, max = 1)
  public static void find(CommandContext cmd, CommandSender sender) {
    String query = cmd.getString(0);
    Optional<User> search = Hook.database().getUsers().findByName(query);
    if (!search.isPresent()) {
      sender.sendMessage(Messages.ERROR_NO_PLAYERS.with(ChatColor.RED));
      return;
    }

    User target = search.get();

    Localizable lastSeen = Sessions.formatLastSeen(sender, target);
    sender.sendMessage(lastSeen);
  }
}
