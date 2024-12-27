package net.avicus.magma.network.user;

import java.util.Optional;
import java.util.UUID;

import com.viaversion.viaversion.api.Via;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.event.user.AsyncHookLoginEvent;
import net.avicus.magma.event.user.AsyncHookLogoutEvent;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class UserListener implements Listener {

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
    UUID uuid = event.getUniqueId();
    Optional<User> query = Magma.get().database().getUsers().findByUuid(uuid);
    boolean newUser = !query.isPresent();

    if (!query.isPresent()) {
      User created = User.createUser(Magma.get().database(), event.getName(), event.getUniqueId());
      query = Optional.of(created);
    }

    User user = query.get();

    if (!user.getName().equals(event.getName())) {
      user.updateUsername(Magma.get().database(), event.getName());
    }

    AsyncHookLoginEvent call = new AsyncHookLoginEvent(event, user, newUser,
        MagmaTranslations.JOIN_DISALLOW_PERMISSION.with(ChatColor.RED));
    Magma.get().getServer().getPluginManager().callEvent(call);

    if (call.isCancelled()) {
      String message = call.getKickMessage().render(null).toLegacyText();
      event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, message);
      return;
    }

    Users.join(user);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerJoin(PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    if (Magma.get().localServer().isPermissible()) {
      String permission = Magma.get().localServer().getPermission().get();

      if (!player.hasPermission(permission)) {
        new BukkitRunnable() {
          @Override
          public void run() {
            player.kickPlayer(MagmaTranslations.JOIN_DISALLOW_PERMISSION.with(ChatColor.RED)
                .render(player).toLegacyText());
          }
        }.runTaskLater(Magma.get(), 5);
        return;
      }
    }

    new BukkitRunnable() {
      @Override
      public void run() {
        if (!player.isOnline()) {
          return;
        }

        User user = Users.user(player);
        user.updateClientPayload(Magma.get().database(), Via.getAPI().getPlayerVersion(player),
            player.spigot().getLocale());
      }
    }.runTaskAsynchronously(Magma.get());
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerLogin(PlayerLoginEvent event) {
    if (Users.getUsers().isEmpty()) {
      // Not localized, locale can't be loaded for the player anyway
      String error = ChatColor.RED + "The server has not loaded, please rejoin in a moment.";
      event.disallow(PlayerLoginEvent.Result.KICK_OTHER, error);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent event) {
    User user = Users.user(event.getPlayer());
    Users.leave(user);

    new BukkitRunnable() {
      @Override
      public void run() {
        AsyncHookLogoutEvent call = new AsyncHookLogoutEvent(user);
        Magma.get().getServer().getPluginManager().callEvent(call);
      }
    }.runTaskAsynchronously(Magma.get());
  }
}
