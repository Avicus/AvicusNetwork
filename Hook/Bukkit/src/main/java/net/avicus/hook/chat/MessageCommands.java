package net.avicus.hook.chat;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.hook.Hook;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.database.model.impl.Session;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.CommandModule;
import net.avicus.magma.network.user.Users;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageCommands implements CommandModule {

  private static boolean isOnline(User user) {
    Session session = Hook.database().getSessions().findLatest(user.getId()).orElse(null);
    return session != null && session.isActive();
  }

  @Command(aliases = {"message",
      "msg"}, min = 2, desc = "Privately message a player.", usage = "<player> <message...>")
  public static void message(CommandContext cmd, CommandSender sender) {
    String query = cmd.getString(0);
    String body = cmd.getJoinedStrings(1);

    HookTask.of(() -> {
      User search = Hook.database().getUsers().findByName(query).orElse(null);

      if (search == null) {
        sender.sendMessage(Messages.ERROR_NO_PLAYERS.with(ChatColor.RED));
        return;
      }

      if (!isOnline(search)) {
        sender.sendMessage(Messages.UI_NOT_ONLINE.with(ChatColor.RED, search.getName()));
        return;
      }

      PrivateMessageConsumer.PrivateMessage message = new PrivateMessageConsumer.PrivateMessage(
          Users.fromSender(sender), search, body);
      Hook.redis().publish(message);
    }).nowAsync();
  }

  @Command(aliases = {"reply", "r",
      "re"}, min = 1, desc = "Privately reply to a player's message.", usage = "<message...>")
  public static void reply(CommandContext cmd, CommandSender sender)
      throws MustBePlayerCommandException {
    MustBePlayerCommandException.ensurePlayer(sender);

    Player player = (Player) sender;
    User target = PrivateMessageConsumer.getLastMessaged(player.getUniqueId()).orElse(null);

    if (target == null) {
      sender.sendMessage(Messages.ERROR_NO_REPLY.with(ChatColor.RED));
      return;
    }

    String body = cmd.getJoinedStrings(0);

    HookTask.of(() -> {
      if (!isOnline(target)) {
        player.sendMessage(Messages.UI_NOT_ONLINE.with(ChatColor.RED, target.getName()));
        return;
      }

      PrivateMessageConsumer.PrivateMessage message = new PrivateMessageConsumer.PrivateMessage(
          Users.user(player), target, body);
      Hook.redis().publish(message);
    }).nowAsync();
  }

  @Override
  public void enable() {
    PlayerSettings.register(PrivateMessageConsumer.PRIVATE_MESSAGES_SETTING);
    Hook.redis().register(new PrivateMessageConsumer());
  }

  @Override
  public void registerCommands(CommandsManagerRegistration registrar) {
    registrar.register(MessageCommands.class);
  }
}
