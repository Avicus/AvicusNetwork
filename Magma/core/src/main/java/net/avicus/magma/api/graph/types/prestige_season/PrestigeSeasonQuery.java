package net.avicus.magma.api.graph.types.prestige_season;

import com.shopify.graphql.support.Query;

public class PrestigeSeasonQuery extends Query<PrestigeSeasonQuery> {

  public PrestigeSeasonQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * When the season ends.
   */
  public PrestigeSeasonQuery endAt() {
    startField("end_at");

    return this;
  }

  /**
   * Value which all XP transactions inside of this season should be multiplied by.
   */
  public PrestigeSeasonQuery multiplier() {
    startField("multiplier");

    return this;
  }

  /**
   * Name of the season.
   */
  public PrestigeSeasonQuery name() {
    startField("name");

    return this;
  }

  /**
   * When the season starts.
   */
  public PrestigeSeasonQuery startAt() {
    startField("start_at");

    return this;
  }
}
