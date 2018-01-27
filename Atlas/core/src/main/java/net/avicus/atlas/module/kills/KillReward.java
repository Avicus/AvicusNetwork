package net.avicus.atlas.module.kills;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.AttackerVariable;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.checks.variable.VictimVariable;
import net.avicus.atlas.module.loadouts.Loadout;
import org.bukkit.entity.Player;

@ToString
public class KillReward {

  private final Loadout loadout;
  private final Optional<Check> check;

  public KillReward(Loadout loadout, Optional<Check> check) {
    this.loadout = loadout;
    this.check = check;
  }

  public void give(Player player) {
    this.loadout.apply(player);
  }

  public boolean passes(Match match, Player killer, Player target) {
    if (this.check.isPresent()) {
      CheckContext context = new CheckContext(match);

      AttackerVariable attacker = new AttackerVariable(match);
      attacker.add(new PlayerVariable(killer));

      VictimVariable victim = new VictimVariable(match);
      victim.add(new PlayerVariable(target));

      context.add(attacker);
      context.add(victim);
      context.add(new LocationVariable(target.getLocation()));

      return this.check.get().test(context).passes();
    } else {
      return true;
    }
  }
}
