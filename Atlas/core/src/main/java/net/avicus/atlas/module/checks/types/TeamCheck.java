package net.avicus.atlas.module.checks.types;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.registry.WeakReference;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.variable.GroupVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.teams.Team;

/**
 * A team check checks the team a player is on.
 */
@ToString(exclude = "match")
public class TeamCheck implements Check {

  private final Match match;
  private final WeakReference<Team> team;

  public TeamCheck(Match match, WeakReference<Team> team) {
    this.match = match;
    this.team = team;
  }

  @Override
  public CheckResult test(CheckContext context) {
    Optional<Team> optional = this.team.getObject();

    if (optional.isPresent()) {
      Optional<GroupVariable> groupVar = context.getFirst(GroupVariable.class);
      Optional<PlayerVariable> playerVar = context.getFirst(PlayerVariable.class);

      if (groupVar.isPresent() || playerVar.isPresent()) {
        if (groupVar.isPresent()) {
          return CheckResult.valueOf(optional.get().equals(groupVar.get().getGroup()));
        } else {
          Group group = this.match.getRequiredModule(GroupsModule.class)
              .getGroup(playerVar.get().getPlayer());
          return CheckResult.valueOf(optional.get().equals(group));
        }
      }
    }

    return CheckResult.IGNORE;
  }
}
