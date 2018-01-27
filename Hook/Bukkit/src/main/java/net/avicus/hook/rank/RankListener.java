package net.avicus.hook.rank;

import java.util.List;
import java.util.Optional;
import net.avicus.hook.Hook;
import net.avicus.magma.database.model.impl.RankMember;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.event.user.AsyncHookLoginEvent;
import net.avicus.magma.event.user.AsyncHookLogoutEvent;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.network.user.rank.BukkitRank;
import net.avicus.magma.network.user.rank.Ranks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class RankListener implements Listener {

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onAsyncLogin(AsyncHookLoginEvent event) {
    Ranks.add(event.getUser(), Ranks.getPermOnly("default").get());
    List<RankMember> memberships = event.getUser().memberships(Hook.database());
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

    event.getPlayer().addAttachment(Hook.plugin(), "bukkit.command.me", false);
    event.getPlayer().addAttachment(Hook.plugin(), "minecraft.command.me", false);
    event.getPlayer().addAttachment(Hook.plugin(), "bukkit.command.tell", false);
    event.getPlayer().addAttachment(Hook.plugin(), "minecraft.command.tell", false);
    event.getPlayer().addAttachment(Hook.plugin(), "worldedit.calc", false);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onAsyncLogout(AsyncHookLogoutEvent event) {
    Ranks.clear(event.getUser());
  }
}
