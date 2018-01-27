package net.avicus.atlas.module.objectives.score;

import java.util.List;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.grave.event.PlayerDeathEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ScoreListener implements Listener {

  private final Match match;
  private final List<ScoreObjective> objectives;

  public ScoreListener(Match match, List<ScoreObjective> objectives) {
    this.match = match;
    this.objectives = objectives;
  }

  @EventHandler
  public void onAtlasKill(PlayerDeathEvent event) {
    if (event.getLifetime().getLastDamage() == null) {
      return;
    }

    LivingEntity damager = event.getLifetime().getLastDamage().getInfo().getResolvedDamager();

    if (!(damager instanceof Player)) {
      return;
    }

    // Same person
    if (damager.getUniqueId().equals(event.getPlayer().getUniqueId())) {
      return;
    }

    Player player = (Player) damager;

    Competitor competitor = this.match.getRequiredModule(GroupsModule.class).getCompetitorOf(player)
        .orElse(null);

    if (competitor == null) {
      return;
    }

    for (ScoreObjective score : this.objectives) {
      if (!score.getKills().isPresent()) {
        return;
      }
      if (!score.canComplete(competitor)) {
        continue;
      }

      int amount = score.getKills().get();
      score.modify(competitor, amount, player);
    }
  }
}
