package net.avicus.atlas.module.compass;

import java.util.Optional;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.util.Messages;
import org.bukkit.entity.Player;

public class DefaultCompassResolver implements CompassResolver {

  @Override
  public Optional<CompassView> resolve(Match match, Player player) {
    CompassView result = new CompassView(player.getLocation(), Messages.UI_COMPASS.with());
    return Optional.of(result);
  }
}
