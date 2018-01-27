package net.avicus.atlas.module.compass;

import java.util.Optional;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.locale.text.Localizable;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PointCompassResolver implements CompassResolver {

  private final Vector point;

  public PointCompassResolver(Vector point) {
    this.point = point;
  }

  @Override
  public Optional<CompassView> resolve(Match match, Player player) {
    Localizable display = Messages.UI_LOCATION_TARGETED.with(ChatColor.GRAY, this.point.toString());
    CompassView result = new CompassView(this.point.toLocation(match.getWorld()), display);
    return Optional.of(result);
  }
}
