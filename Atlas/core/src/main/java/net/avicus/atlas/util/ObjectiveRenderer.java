package net.avicus.atlas.util;

import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.objectives.Objective;
import org.bukkit.entity.Player;

public abstract class ObjectiveRenderer {

  public abstract String getDisplay(Match match, Competitor competitor, Player viewer,
      Objective objective, boolean showName);
}
