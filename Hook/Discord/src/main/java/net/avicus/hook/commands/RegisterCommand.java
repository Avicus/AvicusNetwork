package net.avicus.hook.commands;

import java.util.List;
import java.util.Optional;
import net.avicus.hook.Main;
import net.avicus.magma.database.model.impl.User;

public class RegisterCommand implements DiscordCommand {

  @Override
  public void execute(CommandContext context, List<String> args) throws Exception {
    Optional<User> registered = context.getHook().getUserManagementService()
        .getUser(context.getSender().getIdLong());
    if (registered.isPresent()) {
      context.getLocation()
          .sendMessage("You are already registered! Use !unregister to unregister.").complete();
    } else {
      Main.getHook().getUserManagementService().handleUser(context.getSender(), true);
      context.getLocation().sendMessage(
          "You should receive a private message from the bot with instructions. " +
              "If you did not get a message, check your user settings and ensure the bot is not blocked."
      ).complete();
    }
  }
}
