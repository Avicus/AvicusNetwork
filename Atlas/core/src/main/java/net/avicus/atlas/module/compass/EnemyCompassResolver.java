package net.avicus.atlas.module.compass;

import java.util.Optional;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.locale.text.Localizable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class EnemyCompassResolver implements CompassResolver {

  public EnemyCompassResolver() {

  }

  @Override
  public Optional<CompassView> resolve(Match match, Player player) {
    GroupsModule module = match.getRequiredModule(GroupsModule.class);
    Competitor competitor = module.getCompetitorOf(player).orElse(null);

    double lowest = Double.MAX_VALUE;
    Player found = null;

    for (Player test : Bukkit.getOnlinePlayers()) {
      if (test.getWorld() != player.getWorld()) {
        continue;
      }

      Competitor other = module.getCompetitorOf(test).orElse(null);

      // Either spectator, or they are the same team
      if (other == null || other.equals(competitor)) {
        continue;
      }

      double distance = player.getLocation().distance(test.getLocation());
      if (distance < lowest) {
        lowest = distance;
        found = test;
      }
    }

    if (found == null) {
      return Optional.empty();
    }

    Localizable display = Messages.UI_PLAYER_LOCATED.with(ChatColor.GRAY, found.getDisplayName());
    return Optional.of(new CompassView(found.getLocation().add(0.5, 0, 0.5), display));
  }
}
