package net.avicus.atlas.module.results.scenario;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.elimination.EliminationModule;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.IntegerObjective;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.results.CompetitorCompletionState;
import net.avicus.atlas.module.results.RankingDisplay;
import net.avicus.atlas.module.states.StatesModule;

@ToString
public class ObjectivesScenario extends EndScenario {

  public ObjectivesScenario(Match match, Check check, int places) {
    super(match, check, places);
  }

  @Override
  public void execute(Match match, GroupsModule groups) {
    TreeMap<Integer, HashSet<Competitor>> scores = new TreeMap<>(Collections.reverseOrder());
    scores.put(0, Sets.newHashSet());

    List<CompetitorCompletionState> states = new ArrayList<>();

    for (Competitor competitor : groups.getCompetitors()) {
      states.add(new CompetitorCompletionState(match, competitor));

      int score = 0;
      scores.get(0).add(competitor);

      if (match.hasModule(EliminationModule.class)) {
        score = competitor.getPlayers().size();
      } else {
        // Objectives that this competitor can complete
        List<IntegerObjective> completable = match.getRequiredModule(ObjectivesModule.class)
            .getObjectivesByType(IntegerObjective.class)
            .stream()
            .map((o) -> (IntegerObjective) o)
            .filter(objective -> objective.canComplete(competitor))
            .collect(Collectors.toList());

        if (completable.isEmpty()) {
          continue;
        }

        for (IntegerObjective objective : completable) {
          score += objective.getPoints(competitor);
        }
      }

      if (score != 0) {
        scores.putIfAbsent(score, Sets.newHashSet());

        scores.get(0).remove(competitor);
        scores.get(score).add(competitor);
      }
    }

    TreeMap<Integer, List<CompetitorCompletionState>> ranked = CompetitorCompletionState
        .getRankedCompletions(states);

    // Empty match
    if (ranked.isEmpty()) {
      new TieScenario(match, this.getCheck(), this.getPlaces()).execute(match, groups);
      return;
    }

    // This means no one has done anything.
    if (!ranked.firstEntry().getValue().get(0).hasDoneAnything()
        && scores.firstEntry().getKey() == 0) {
      new TieScenario(match, this.getCheck(), this.getPlaces()).execute(match, groups);
      return;
    }

    if (scores.firstEntry().getKey() != 0) {
      processWin(scores, match, groups);
    } else {
      processStatesWin(ranked, match, groups);
    }
  }

  private void processWin(TreeMap<Integer, HashSet<Competitor>> ranked, Match match,
      GroupsModule groups) {
    if (getPlaces() == 1) {
      if (ranked.firstEntry().getValue().size() > 1) {
        new TieScenario(match, this.getCheck(), this.getPlaces()).execute(match, groups);
        return;
      } else {
        handleWin(match, ranked.firstEntry().getValue().iterator().next());
        match.getRequiredModule(StatesModule.class).next();
        return;
      }
    }

    match.getRequiredModule(StatesModule.class).next();

    RankingDisplay display = new RankingDisplay(getPlaces(), ranked);
    handleMultiWin(match, display);
  }

  private void processStatesWin(TreeMap<Integer, List<CompetitorCompletionState>> completions,
      Match match, GroupsModule groups) {
    if (getPlaces() == 1) {
      if (completions.firstEntry().getValue().isEmpty()) {
        new TieScenario(match, this.getCheck(), this.getPlaces()).execute(match, groups);
        return;
      }
      if (completions.firstEntry().getValue().size() > 1 && !completions.firstEntry().getValue()
          .get(0).hasDoneAnything()) {
        new TieScenario(match, this.getCheck(), this.getPlaces()).execute(match, groups);
        return;
      } else {
        handleWin(match, completions.firstEntry().getValue().get(0).getCompetitor());
        match.getRequiredModule(StatesModule.class).next();
        return;
      }
    }

    match.getRequiredModule(StatesModule.class).next();

    TreeMap<Integer, HashSet<Competitor>> normalizedRanks = Maps.newTreeMap();

    completions.forEach((i, l) ->
        normalizedRanks.put(i, Sets.newHashSet(
            l.stream().map(CompetitorCompletionState::getCompetitor).collect(Collectors.toList())))
    );

    RankingDisplay display = new RankingDisplay(getPlaces(), normalizedRanks);
    handleMultiWin(match, display);
  }
}