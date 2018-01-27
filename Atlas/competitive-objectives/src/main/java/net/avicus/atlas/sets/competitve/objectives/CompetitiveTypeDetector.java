package net.avicus.atlas.sets.competitve.objectives;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import net.avicus.atlas.GameType;
import net.avicus.atlas.map.AtlasMap;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.sets.competitve.objectives.bridges.ObjectivesBridge;
import net.avicus.atlas.sets.competitve.objectives.destroyable.leakable.LeakableObjective;
import net.avicus.atlas.sets.competitve.objectives.destroyable.monument.MonumentObjective;
import net.avicus.atlas.sets.competitve.objectives.hill.HillObjective;
import net.avicus.atlas.sets.competitve.objectives.wool.WoolObjective;
import net.avicus.magma.util.MapGenre;

public class CompetitiveTypeDetector implements AtlasMap.TypeDetector {

  @Override
  public Optional<MapGenre> detectGenre(Match match) {
    Optional<ObjectivesModule> module = match.getModule(ObjectivesModule.class);

    if (module.isPresent()) {
      ObjectivesBridge objectives = match.getRequiredModule(ObjectivesModule.class)
          .getBridge(ObjectivesBridge.class);
      if (objectives.getFlags().size() == objectives.getObjectives().size()
          || objectives.getFlags().size() + objectives.getModule().getScores().size() == objectives
          .getObjectives().size()) {
        return Optional.of(MapGenre.CTF);
      }
      if (objectives.getMonuments().size() + objectives.getWools().size() + objectives
          .getLeakables().size() == objectives.getObjectives().size()) {
        return Optional.of(MapGenre.NEBULA);
      }
      if (objectives.getHills().size() == objectives.getObjectives().size()
          || objectives.getHills().size() + objectives.getModule().getScores().size() == objectives
          .getObjectives().size()) {
        return Optional.of(MapGenre.KOTH);
      }
    }

    return Optional.empty();
  }

  @Override
  public Set<GameType> detectGameTypes(Match match) {
    Set<GameType> types = Sets.newHashSet();

    match.getModule(ObjectivesModule.class).ifPresent(objectives -> {
      for (final Objective objective : objectives.getObjectives()) {
        if (objective instanceof WoolObjective) {
          types.add(GameType.CTW);
        } else if (objective instanceof LeakableObjective) {
          types.add(GameType.DTC);
        } else if (objective instanceof MonumentObjective) {
          types.add(GameType.DTM);
        } else if (objective instanceof HillObjective) {
          types.add(GameType.HILL);
        }
      }
    });

    return types;
  }
}
