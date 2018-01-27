package net.avicus.magma.network.user.rank;

import java.util.List;
import java.util.Optional;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.RankMember;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.event.user.AsyncHookLoginEvent;
import net.avicus.magma.event.user.AsyncHookLogoutEvent;
import net.avicus.magma.network.user.Users;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

public class RankListener implements Listener {

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onAsyncLogin(AsyncHookLoginEvent event) {
    Ranks.add(event.getUser(), Ranks.getPermOnly("default").get());
    List<RankMember> memberships = event.getUser().memberships(Magma.get().database());
    for (RankMember membership : memberships) {
      Optional<BukkitRank> rank = Ranks.getCached(membership.getRankId());
      if (rank.isPresent()) {
        Ranks.add(event.getUser(), rank.get());
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerJoin(PlayerJoinEvent event) {
    User user = Users.user(event.getPlayer());
    List<BukkitRank> ranks = Ranks.get(user);
    for (BukkitRank bukkit : ranks) {
      bukkit.attachPermissions(event.getPlayer());
    }

    Plugin plugin = Magma.get();
    PermissionAttachment attachment = event.getPlayer().addAttachment(plugin);
    attachment.setPermission("bukkit.command.me", false);
    attachment.setPermission("minecraft.command.me", false);
    attachment.setPermission("bukkit.command.tell", false);
    attachment.setPermission("minecraft.command.tell", false);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onAsyncLogout(AsyncHookLogoutEvent event) {
    Ranks.clear(event.getUser());
  }
}
