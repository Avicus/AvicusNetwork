package net.avicus.magma.api.graph.types.map_rating;

import com.shopify.graphql.support.Query;

public class MapRatingQuery extends Query<MapRatingQuery> {

  public MapRatingQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Date when this maprating was created.
   */
  public MapRatingQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * Feedback entered in the feedback book.
   */
  public MapRatingQuery feedback() {
    startField("feedback");

    return this;
  }

  /**
   * Slug of the map which this rating is for.
   */
  public MapRatingQuery mapSlug() {
    startField("map_slug");

    return this;
  }

  /**
   * Version of the map which this rating is for.
   */
  public MapRatingQuery mapVersion() {
    startField("map_version");

    return this;
  }

  /**
   * ID of the user who rated this map.
   */
  public MapRatingQuery player() {
    startField("player");

    return this;
  }

  /**
   * Rating which the user gave for this map version.
   */
  public MapRatingQuery rating() {
    startField("rating");

    return this;
  }

  /**
   * Date when this maprating was last updated.
   */
  public MapRatingQuery updatedAt() {
    startField("updated_at");

    return this;
  }
}
