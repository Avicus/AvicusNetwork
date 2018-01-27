package net.avicus.hook.commands;

import java.util.List;
import java.util.Optional;
import net.avicus.magma.database.model.impl.User;

public class UnRegisterCommand implements DiscordCommand {

  @Override
  public void execute(CommandContext context, List<String> args) throws Exception {
    Optional<User> registered = context.getHook().getUserManagementService()
        .getUser(context.getSender().getIdLong());
    if (registered.isPresent()) {
      registered.get().resetDiscord(context.getHook().getDatabase());
      context.getLocation()
          .sendMessage("You are no longer registered as " + registered.get().getName()).complete();
      context.getHook().getUserManagementService().getUserMap()
          .remove(context.getSender().getIdLong());
      context.getHook().getUserManagementService().handleUser(context.getSender(), true);
    } else {
      context.getLocation().sendMessage("You must be registered to use that command!").complete();
    }
  }
}
