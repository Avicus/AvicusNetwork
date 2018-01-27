package net.avicus.atlas.sets.competitve.objectives.hill;

import java.util.List;
import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import net.avicus.atlas.event.match.MatchStateChangeEvent;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.grave.event.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import tc.oc.tracker.event.PlayerCoarseMoveEvent;

public class HillListener implements Listener {

  private final ObjectivesModule module;
  private final List<HillObjective> hills;
  private final HillTask task;

  public HillListener(ObjectivesModule module, List<HillObjective> hills) {
    this.module = module;
    this.hills = hills;
    this.task = new HillTask(module);
  }

  @EventHandler
  public void onPlayerCoarseMove(PlayerCoarseMoveEvent event) {
    if (this.module.getMatch().getRequiredModule(GroupsModule.class)
        .isObservingOrDead(event.getPlayer())) {
      return;
    }

    Player player = event.getPlayer();

    for (HillObjective hill : this.hills) {
      if (!hill.canCapture(player)) {
        continue;
      }

      boolean inside = hill.getCapture().contains(event.getTo().getBlock());

      if (inside) {
        hill.add(player);
      } else {
        hill.remove(player);
      }
    }
  }

  @EventHandler
  public void onStateChange(MatchStateChangeEvent event) {
    this.task.cancel0();

    if (event.isToPlaying()) {
      this.task.start();
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    for (HillObjective hill : this.hills) {
      hill.remove(event.getPlayer());
    }
  }

  @EventHandler
  public void onChangeTeam(PlayerChangedGroupEvent event) {
    if (!event.getGroupFrom().isPresent()) {
      return;
    }

    for (HillObjective hill : this.hills) {
      hill.remove(event.getPlayer());
    }
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    for (HillObjective hill : this.hills) {
      hill.remove(event.getPlayer());
    }
  }
}
