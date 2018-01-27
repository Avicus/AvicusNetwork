package net.avicus.magma.api.graph.types.prestige_level;

import com.shopify.graphql.support.Query;

public class PrestigeLevelQuery extends Query<PrestigeLevelQuery> {

  public PrestigeLevelQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * The level that was reached.
   */
  public PrestigeLevelQuery level() {
    startField("level");

    return this;
  }

  /**
   * ID of the season which this happened in.
   */
  public PrestigeLevelQuery seasonId() {
    startField("season_id");

    return this;
  }

  /**
   * ID of the user who reached the level.
   */
  public PrestigeLevelQuery userId() {
    startField("user_id");

    return this;
  }
}
