package net.avicus.hook.backend.buycraft.packages;

import java.util.Map;
import net.avicus.hook.Hook;
import net.avicus.hook.backend.buycraft.BuycraftPackage;
import net.avicus.magma.announce.AnnounceMessageHandler;
import net.avicus.magma.database.model.impl.User;
import net.md_5.bungee.api.chat.BaseComponent;

public class AnnouncePackage implements BuycraftPackage {

  private final BaseComponent[] message;

  public AnnouncePackage(BaseComponent... message) {
    this.message = message;
  }

  @Override
  public void execute(Status status, User user, Map<String, String> variables) {
    if (status != Status.INITIAL) {
      return;
    }

    Hook.redis().publish(new AnnounceMessageHandler.AnnounceMessage(this.message,
        AnnounceMessageHandler.AnnounceType.MESSAGE, Hook.server()));
  }
}
