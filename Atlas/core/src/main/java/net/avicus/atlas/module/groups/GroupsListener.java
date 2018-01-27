package net.avicus.atlas.module.groups;

import java.util.Optional;
import net.avicus.atlas.event.match.MatchCloseEvent;
import net.avicus.atlas.event.match.MatchOpenEvent;
import net.avicus.atlas.event.match.MatchStateChangeEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.spawns.SpawnsModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GroupsListener implements Listener {

  private final GroupsModule module;

  public GroupsListener(GroupsModule module) {
    this.module = module;
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerJoin(PlayerJoinEvent event) {
    this.module
        .changeGroup(event.getPlayer(), Optional.empty(), this.module.getSpectators(), true, true);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onMatchOpen(MatchOpenEvent event) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      this.module
          .changeGroup(player, Optional.empty(), this.module.getSpectators(), true, true, false);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onMatchClose(MatchCloseEvent event) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      Group group = this.module.getGroup(player);
      group.remove(player);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerQuit(PlayerQuitEvent event) {
    Group group = this.module.getGroup(event.getPlayer());
    group.remove(event.getPlayer());
  }

  @EventHandler
  public void onMatchStateChange(MatchStateChangeEvent event) {
    if (!event.getTo().isPresent()) {
      return;
    }

    for (Group team : this.module.getGroups()) {
      boolean observing = !event.getTo().get().isPlaying();
      team.setObserving(observing);
    }

    Match match = event.getMatch();
    Spectators spectators = this.module.getSpectators();

    if (event.isChangeToPlaying()) {
      for (Group group : this.module.getGroups()) {
        if (group.isSpectator()) {
          continue;
        }

        for (Player player : group.getPlayers()) {
          match.getRequiredModule(SpawnsModule.class).spawn(group, player, true, true);
        }
      }
    } else if (event.isChangeToNotPlaying()) {
      for (Group group : this.module.getGroups()) {
        if (group.isSpectator()) {
          continue;
        }

        for (Player player : group.getPlayers()) {
          match.getRequiredModule(SpawnsModule.class).spawn(spectators, player, true, false);
//                    match.getRequiredModule(SpawnsModule.class).spawn(group, player, false, false);
        }
      }
    }
  }
}
