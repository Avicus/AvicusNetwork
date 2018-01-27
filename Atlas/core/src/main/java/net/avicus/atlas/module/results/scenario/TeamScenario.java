package net.avicus.atlas.module.results.scenario;

import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.states.StatesModule;

@ToString
public class TeamScenario extends EndScenario {

  @Getter
  private final Team team;

  public TeamScenario(Match match, Check check, int places, Team team) {
    super(match, check, places);
    this.team = team;
  }

  @Override
  public void execute(Match match, GroupsModule groups) {
    match.getRequiredModule(StatesModule.class).next();

    for (Competitor competitor : groups.getCompetitors(getTeam())) {
      handleWin(match, competitor);
    }
  }
}
