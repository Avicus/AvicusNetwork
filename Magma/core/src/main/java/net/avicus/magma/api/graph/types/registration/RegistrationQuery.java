package net.avicus.magma.api.graph.types.registration;

import com.shopify.graphql.support.Query;

public class RegistrationQuery extends Query<RegistrationQuery> {

  public RegistrationQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * If this registration has been accepted by a tournament staff member.
   */
  public RegistrationQuery status() {
    startField("status");

    return this;
  }

  /**
   * ID of the team who is attempting to register.
   */
  public RegistrationQuery teamId() {
    startField("team_id");

    return this;
  }

  /**
   * ID of the tournament which this registration is for.
   */
  public RegistrationQuery tournamentId() {
    startField("tournament_id");

    return this;
  }

  /**
   * Data about which users (denoted by ID) have accepted the invite.
   */
  public RegistrationQuery userData() {
    startField("user_data");

    return this;
  }
}
