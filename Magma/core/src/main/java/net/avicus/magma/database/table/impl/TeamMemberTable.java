package net.avicus.magma.database.table.impl;

import java.util.List;
import java.util.Optional;
import net.avicus.magma.database.model.impl.TeamMember;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;
import net.avicus.quest.query.Filter;

public class TeamMemberTable extends Table<TeamMember> {

  public TeamMemberTable(Database database, String name, Class<TeamMember> model) {
    super(database, name, model);
  }

  /**
   * Find the accepted team member of a user.
   */
  public Optional<TeamMember> findAcceptedByUser(int userId) {
    return select().where("user_id", userId).where("accepted", true).execute().stream().findFirst();
  }

  /**
   * Find all member associations of a user. More than one is possible
   * due to old validation errors. For example, a player have been invited to
   * Team A, accepted to Team A, then invited to Team B.
   *
   * @param userId The user's id.
   * @return The team members.
   */
  public List<TeamMember> findByUser(int userId) {
    return select().where("user_id", userId).execute();
  }

  /**
   * Find all members of a team.
   *
   * @param teamId The team's id.
   * @param onlyAccepted If true, only accepted team members will be returned.
   */
  public List<TeamMember> findByTeam(int teamId, boolean onlyAccepted) {
    Filter filter = new Filter("team_id", teamId);

    if (onlyAccepted) {
      filter.and("accepted", true);
    }

    return select().where(filter).execute();
  }
}
