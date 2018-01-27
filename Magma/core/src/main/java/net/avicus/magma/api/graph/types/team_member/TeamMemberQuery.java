package net.avicus.magma.api.graph.types.team_member;

import com.shopify.graphql.support.Query;

public class TeamMemberQuery extends Query<TeamMemberQuery> {

  public TeamMemberQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * If the user accepted the invitation to join the team.
   */
  public TeamMemberQuery accepted() {
    startField("accepted");

    return this;
  }

  /**
   * Date that the user acceoted the invitation to join the team.
   */
  public TeamMemberQuery acceptedAt() {
    startField("accepted_at");

    return this;
  }

  /**
   * Date when this teammember was created.
   */
  public TeamMemberQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * Role of the user on the team
   */
  public TeamMemberQuery role() {
    startField("role");

    return this;
  }

  /**
   * ID of the team that the user is on.
   */
  public TeamMemberQuery teamId() {
    startField("team_id");

    return this;
  }

  /**
   * ID of the user who is on the team.
   */
  public TeamMemberQuery userId() {
    startField("user_id");

    return this;
  }
}
