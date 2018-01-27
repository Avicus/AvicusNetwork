package net.avicus.atlas.module.compass;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import org.bukkit.entity.Player;

@ToString
public class Compass implements CompassResolver {

  private final CompassResolver resolver;
  private final Optional<Check> check;

  public Compass(CompassResolver resolver, Optional<Check> check) {
    this.resolver = resolver;
    this.check = check;
  }

  public boolean passes(Match match, Player player) {
    if (!this.check.isPresent()) {
      return true;
    }

    CheckContext context = new CheckContext(match);
    context.add(new PlayerVariable(player));
    return this.check.get().test(context).passes();
  }

  @Override
  public Optional<CompassView> resolve(Match match, Player player) {
    return this.resolver.resolve(match, player);
  }
}
