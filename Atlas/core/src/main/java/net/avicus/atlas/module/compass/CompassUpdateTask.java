package net.avicus.atlas.module.compass;

import java.util.List;
import java.util.Optional;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.util.AtlasTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CompassUpdateTask extends AtlasTask {

  private final Match match;
  private final CompassModule module;
  private final List<Compass> compasses;

  public CompassUpdateTask(Match match, CompassModule module, List<Compass> compasses) {
    super();
    this.match = match;
    this.module = module;
    this.compasses = compasses;
  }

  @Override
  public void run() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (!this.match.getRequiredModule(GroupsModule.class).isObservingOrDead(player)) {
        Optional<CompassView> result = Optional.empty();
        for (Compass compass : this.compasses) {
          if (compass.passes(this.match, player)) {
            result = compass.resolve(this.match, player);

            if (result.isPresent()) {
              break;
            }
          }
        }
        this.module.setCompasses(player, result);
      }

    }
  }
}
