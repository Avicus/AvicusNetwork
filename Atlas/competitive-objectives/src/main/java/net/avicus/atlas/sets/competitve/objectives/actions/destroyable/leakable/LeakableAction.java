package net.avicus.atlas.sets.competitve.objectives.actions.destroyable.leakable;

import net.avicus.atlas.sets.competitve.objectives.actions.destroyable.base.DestroyableAction;
import net.avicus.atlas.sets.competitve.objectives.destroyable.leakable.LeakableObjective;

public interface LeakableAction extends DestroyableAction {

  LeakableObjective getLeakable();
}
