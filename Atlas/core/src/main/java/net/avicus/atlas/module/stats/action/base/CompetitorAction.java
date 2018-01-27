package net.avicus.atlas.module.stats.action.base;

import net.avicus.atlas.module.groups.Competitor;

public interface CompetitorAction extends Action {

  Competitor getActor();
}
