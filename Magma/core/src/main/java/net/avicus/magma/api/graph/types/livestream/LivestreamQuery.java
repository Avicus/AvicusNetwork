package net.avicus.magma.api.graph.types.livestream;

import com.shopify.graphql.support.Query;

public class LivestreamQuery extends Query<LivestreamQuery> {

  public LivestreamQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Twitch username of the streamer.
   */
  public LivestreamQuery channel() {
    startField("channel");

    return this;
  }

  /**
   * Date when this livestream was created.
   */
  public LivestreamQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * Date when this livestream was last updated.
   */
  public LivestreamQuery updatedAt() {
    startField("updated_at");

    return this;
  }
}
