package net.avicus.atlas.sets.competitve.objectives.actions.destroyable.monument;

import net.avicus.atlas.sets.competitve.objectives.actions.destroyable.base.DestroyableAction;
import net.avicus.atlas.sets.competitve.objectives.destroyable.monument.MonumentObjective;

public interface MonumentAction extends DestroyableAction {

  MonumentObjective getMonument();
}
