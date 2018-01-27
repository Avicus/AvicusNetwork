package net.avicus.atlas.module.compass;

import java.util.Optional;
import net.avicus.atlas.match.Match;
import org.bukkit.entity.Player;

public interface CompassResolver {

  Optional<CompassView> resolve(Match match, Player player);
}
