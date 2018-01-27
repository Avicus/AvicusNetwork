package net.avicus.atlas.module.objectives;

import java.util.Optional;
import net.avicus.atlas.module.groups.Competitor;

public interface StagnatedCompletionObjective extends GlobalObjective {

  Optional<Competitor> getHighestCompleter();


}
