package net.avicus.magma.api.graph.types.tournament;

import com.shopify.graphql.support.Query;

public class TournamentQuery extends Query<TournamentQuery> {

  public TournamentQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Raw HTML of the about section of the tournament.
   */
  public TournamentQuery about() {
    startField("about");

    return this;
  }

  /**
   * If this tournament allows individual users to register.
   */
  public TournamentQuery allowLoners() {
    startField("allow_loners");

    return this;
  }

  /**
   * Time when registration closes.
   */
  public TournamentQuery closeAt() {
    startField("close_at");

    return this;
  }

  /**
   * Date when this tournament was created.
   */
  public TournamentQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * If the tournament header should be shown in the UI.
   */
  public TournamentQuery header() {
    startField("header");

    return this;
  }

  /**
   * Maximum number of players allowed to play for each team.
   */
  public TournamentQuery max() {
    startField("max");

    return this;
  }

  /**
   * Minimum number of players allowed to play for each team.
   */
  public TournamentQuery min() {
    startField("min");

    return this;
  }

  /**
   * The name of the tournament
   */
  public TournamentQuery name() {
    startField("name");

    return this;
  }

  /**
   * Time when registration opens.
   */
  public TournamentQuery openAt() {
    startField("open_at");

    return this;
  }

  /**
   * The slug of the tournament used in the URL.
   */
  public TournamentQuery slug() {
    startField("slug");

    return this;
  }
}
