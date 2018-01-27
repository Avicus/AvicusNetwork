package net.avicus.magma.api.graph.types.death;

import com.shopify.graphql.support.Query;

public class DeathQuery extends Query<DeathQuery> {

  public DeathQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * ID of the user who caused this death.
   */
  public DeathQuery cause() {
    startField("cause");

    return this;
  }

  /**
   * If the cause has hidden this death (their kill, respectively) with a stats reset.
   */
  public DeathQuery causeHidden() {
    startField("cause_hidden");

    return this;
  }

  /**
   * Date when this death was created.
   */
  public DeathQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * If the user has hidden this death with a stats reset.
   */
  public DeathQuery userHidden() {
    startField("user_hidden");

    return this;
  }

  /**
   * ID of the user who died.
   */
  public DeathQuery userId() {
    startField("user_id");

    return this;
  }
}
