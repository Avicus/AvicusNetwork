package net.avicus.atlas.module.objectives.lts;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.objectives.GlobalObjective;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.states.StatesModule;

@ToString(exclude = "match")
public class LastTeamStanding implements Objective, GlobalObjective {

  private final Match match;
  @Getter
  private final Team team;
  private final List<Team> otherTeams;

  public LastTeamStanding(Match match, Team team) {
    this.match = match;
    this.team = team;
    this.otherTeams = new ArrayList<>();
    this.otherTeams.addAll(this.match.getRequiredModule(GroupsModule.class)
        .getGroups()
        .stream()
        .filter(group -> group instanceof Team && !group.equals(this.team))
        .map(group -> (Team) group)
        .collect(Collectors.toList()));
  }

  @Override
  public void initialize() {

  }

  @Override
  public LocalizedXmlString getName() {
    return new LocalizedXmlString("Last Team Standing: {0}", this.getTeam().getName().toText());
  }

  @Override
  public boolean canComplete(Competitor competitor) {
    return this.team.equals(competitor.getGroup());
  }

  @Override
  public boolean isCompleted(Competitor competitor) {
    if (!canComplete(competitor)) {
      throw new IllegalArgumentException("Competitor cannot complete objective.");
    }
    return isCompleted();
  }

  @Override
  public double getCompletion(Competitor competitor) {
    if (!canComplete(competitor)) {
      throw new IllegalArgumentException("Competitor cannot complete objective.");
    }
    return 0.0;
  }

  @Override
  public boolean isIncremental() {
    return false;
  }

  @Override
  public boolean isCompleted() {
    if (this.match.getRequiredModule(StatesModule.class).isStarting()) {
      return false;
    }
    for (Team team : this.otherTeams) {
      if (team.getMembers().size() > 0) {
        return false;
      }
    }
    return true;
  }

  @Override
  public double getCompletion() {
    return 0.0;
  }
}
