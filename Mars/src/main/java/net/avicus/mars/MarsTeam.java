package net.avicus.mars;

import java.util.List;
import java.util.Optional;
import lombok.ToString;
import net.avicus.magma.database.model.impl.Team;
import net.avicus.magma.database.model.impl.TeamMember;

@ToString
public class MarsTeam {

  private final Team team;
  private final List<TeamMember> members;

  /**
   * Constructor
   *
   * @param team The database team.
   * @param members List of _accepted_ team members.
   */
  public MarsTeam(Team team, List<TeamMember> members) {
    this.team = team;
    this.members = members;
  }

  public int getId() {
    return this.team.getId();
  }

  public String getName() {
    return this.team.getName();
  }

  public Optional<TeamMember> getMember(int userId) {
    return this.members.stream().filter((member) -> member.getUserId() == userId).findAny();
  }

  public Optional<TeamMember.Role> getRole(int userId) {
    return getMember(userId).map(TeamMember::getRole);
  }

  public boolean isMember(int userId) {
    return getMember(userId).isPresent();
  }
}
