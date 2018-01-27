package net.avicus.atlas.module.checks.variable;

import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.Variable;

/**
 * The victim variable is a wrapper variable that contains player and location information about the
 * victim of an attack.
 */
@ToString(callSuper = true)
public class VictimVariable extends CheckContext implements Variable {

  public VictimVariable(Match match) {
    super(match);
  }
}
