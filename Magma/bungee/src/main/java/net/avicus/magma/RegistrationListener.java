package net.avicus.magma;

import com.sk89q.minecraft.util.commands.ChatColor;
import java.util.UUID;
import net.avicus.magma.database.Database;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class RegistrationListener implements Listener {

  public static final String AUTHENTICATED = ChatColor.translateAlternateColorCodes('&',
      "&aUser authenticated!\n\n&fYou can go back to your browser and \n&fcomplete the registration process.");
  public static final String INVALID = ChatColor.translateAlternateColorCodes('&',
      "&cInvalid IP address!\n\n&fEnsure you used the correct IP.");

  private final Database database;

  public RegistrationListener(Database database) {
    this.database = database;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onRegister(ServerConnectEvent event) {
    String host = event.getPlayer().getPendingConnection().getVirtualHost().getHostString();

    if (host.contains(".register." + NetworkIdentification.URL)) {
      String verifyKey = host.split("\\.")[0];
      UUID uuid = event.getPlayer().getUniqueId();

      boolean registered = this.database.getUsers().registerProfile(uuid, verifyKey);

      String message = registered ? AUTHENTICATED : INVALID;

      event.getPlayer().disconnect(message);
    }
  }
}
