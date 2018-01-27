package net.avicus.atlas.module.decay;

import java.util.List;
import lombok.Getter;
import net.avicus.atlas.event.match.MatchStateChangeEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.util.AtlasTask;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import tc.oc.tracker.event.PlayerCoarseMoveEvent;

public class DecayModule implements Module {

  @Getter
  private final List<DecayArea> areas;
  private final AtlasTask stationaryPlayerTask;
  private Match match;

  public DecayModule(Match match, List<DecayArea> areas) {
    this.match = match;
    this.areas = areas;

    this.stationaryPlayerTask = AtlasTask.of(() -> {
      if (!match.getRequiredModule(StatesModule.class).isPlaying()) {
        return;
      }

      for (Competitor competitor : match.getRequiredModule(GroupsModule.class).getCompetitors()) {
        for (Player player : competitor.getPlayers()) {
          if (match.getRequiredModule(GroupsModule.class).isObservingOrDead(player)) {
            return;
          }

          for (DecayArea area : this.areas) {
            if (area.getRegion().contains(player)) {
              Location below = player.getLocation().clone().add(0, -1, 0);

              area.decay(below);

              break;
            }
          }

        }
      }
    });
  }

  @EventHandler
  public void onMove(PlayerCoarseMoveEvent event) {
    if (match.getRequiredModule(GroupsModule.class).isObservingOrDead(event.getPlayer())) {
      return;
    }

    for (DecayArea area : this.areas) {
      if (area.getRegion().contains(event.getPlayer())) {
        Location below = event.getPlayer().getLocation().clone().add(0, -1, 0);

        area.decay(below);

        break;
      }
    }
  }

  @EventHandler
  public void startStopTask(MatchStateChangeEvent event) {
    if (!event.isFromPlaying() && event.isChangeToPlaying()) {
      this.stationaryPlayerTask.repeat(0, 20 * 6);
    } else if (event.isChangeToPlaying() && event.isFromPlaying()) {
      this.stationaryPlayerTask.cancel0();
    }
  }
}
