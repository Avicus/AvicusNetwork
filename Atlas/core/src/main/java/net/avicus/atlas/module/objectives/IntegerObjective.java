package net.avicus.atlas.module.objectives;

import javax.annotation.Nullable;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.compendium.number.NumberAction;
import org.bukkit.entity.Player;

public interface IntegerObjective extends Objective {

  public int getPoints(Competitor competitor);

  public void modify(Competitor competitor, int amount, NumberAction action,
      @Nullable Player actor);
}
