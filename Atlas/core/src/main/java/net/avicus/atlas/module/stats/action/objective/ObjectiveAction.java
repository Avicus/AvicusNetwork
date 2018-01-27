package net.avicus.atlas.module.stats.action.objective;

import java.time.Instant;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.stats.action.base.Action;
import net.avicus.compendium.locale.text.LocalizedFormat;

@ToString
public abstract class ObjectiveAction implements Action {

  @Getter
  private final Objective acted;
  @Getter
  private final Instant when;

  public ObjectiveAction(Objective acted, Instant when) {
    this.acted = acted;
    this.when = when;
  }

  public abstract LocalizedFormat actionMessage(boolean plural);
}
