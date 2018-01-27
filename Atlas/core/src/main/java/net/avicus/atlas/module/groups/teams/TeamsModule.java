package net.avicus.atlas.module.groups.teams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.CompetitorRule;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.Spectators;

@ToString(callSuper = true)
public class TeamsModule extends GroupsModule {

  private final List<Team> teams;
  private final List<Group> groups;
  private final CompetitorRule competitorRule;

  public TeamsModule(Match match, List<Team> teams, CompetitorRule competitorRule,
      Spectators spectators, boolean lockPlayers) {
    super(match, lockPlayers);
    this.teams = teams;
    this.competitorRule = competitorRule;

    this.groups = new ArrayList<>(teams);
    this.groups.add(0, spectators); // add it first
  }

  @Override
  public CompetitorRule getCompetitorRule() {
    return this.competitorRule;
  }

  @Override
  public Collection<Group> getGroups() {
    return this.groups;
  }

  @Override
  public Collection<? extends Competitor> getCompetitors() {
    if (this.competitorRule == CompetitorRule.TEAM) {
      return this.teams;
    } else {
      List<Competitor> players = new ArrayList<>();
      for (Team team : this.teams) {
        players.addAll(team.getMembers());
      }
      return players;
    }
  }

  public Collection<? extends Competitor> getCompetitors(Group group) {
    if (group instanceof Spectators) {
      return Collections.emptyList();
    }

    if (this.competitorRule == CompetitorRule.TEAM) {
      return Collections.singletonList((Team) group);
    } else {
      return group.getMembers();
    }
  }
}
