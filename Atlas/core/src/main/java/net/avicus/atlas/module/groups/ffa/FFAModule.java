package net.avicus.atlas.module.groups.ffa;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.CompetitorRule;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.Spectators;

@ToString(callSuper = true)
public class FFAModule extends GroupsModule {

  @Getter
  private final FFATeam team;
  private final List<Group> groups;

  public FFAModule(Match match, FFATeam team, Spectators spectators) {
    super(match, false);
    this.team = team;
    this.groups = Arrays.asList(team, spectators);
  }

  @Override
  public CompetitorRule getCompetitorRule() {
    return CompetitorRule.INDIVIDUAL;
  }

  @Override
  public Collection<? extends Group> getGroups() {
    return this.groups;
  }

  @Override
  public Collection<? extends Competitor> getCompetitors() {
    return this.team.getMembers();
  }

  @Override
  public Collection<? extends Competitor> getCompetitors(Group group) {
    return group.getMembers();
  }
}
