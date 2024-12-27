package net.avicus.hook.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import java.util.List;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import net.avicus.compendium.locale.text.LocalizedTime;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.utils.Strings;
import net.avicus.hook.Hook;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.database.model.impl.Username;
import net.avicus.magma.network.user.Users;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UserCommands {

  @CommandPermissions("hook.namehistory")
  @Command(aliases = {"namehistory",
      "nh"}, desc = "View a user's username history.", usage = "<player>", min = 1, max = 1)
  public static void nameHistory(CommandContext cmd, CommandSender sender) {
    String query = cmd.getString(0);

    HookTask.of(() -> {

      User user = Hook.database().getUsers().findByName(query).orElse(null);
      if (user == null) {
        sender.sendMessage(Messages.ERROR_NO_PLAYERS.with(ChatColor.RED));
        return;
      }

      sender.sendMessage(Strings.padChatComponent(
          Messages.UI_NAME_HISTORY.with(ChatColor.BLUE, Users.getDisplay(user))
              .render(sender), "-", ChatColor.YELLOW, ChatColor.AQUA));

      List<Username> names = Hook.database().getUsernames().findByUser(user);

      for (Username username : names) {
        UnlocalizedFormat format = new UnlocalizedFormat("{0} ({1})");
        UnlocalizedText text = new UnlocalizedText(username.getName(), ChatColor.DARK_AQUA);
        LocalizedTime time = new LocalizedTime(username.getCreatedAt());
        time.style().color(ChatColor.GOLD);

        sender.sendMessage(format.with(ChatColor.GRAY, text, time));
      }
    }).nowAsync();

  }

  @Command(aliases = {
      "tsregister"}, desc = "Register yourself with a teamspeak client.", usage = "<auth>", min = 1, max = 1)
  public static void tsRegister(CommandContext cmd, CommandSender sender)
      throws MustBePlayerCommandException {
    MustBePlayerCommandException.ensurePlayer(sender);

    String auth = cmd.getString(0);

    HookTask.of(() -> {

      String res = Hook.redis().get("ts-reg-a." + auth);

      if (res == null || res.isEmpty()) {
        sender.sendMessage(Messages.ERROR_AUTH_NOT_FOUND.with(ChatColor.RED));
        return;
      }

      Hook.redis().set("ts-reg-v." + auth, Integer.toString(Users.user((Player) sender).getId()));
      Hook.redis().del("ts-reg-a." + auth);

      sender.sendMessage(Messages.GENERIC_REGISTERED.with(ChatColor.GOLD, res));
    }).nowAsync();
  }
}
