package net.avicus.magma.api.graph.types.server_category;

import com.shopify.graphql.support.Query;

public class ServerCategoryQuery extends Query<ServerCategoryQuery> {

  public ServerCategoryQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Options related to how servers in this category should communicate both outward and inside the
   * category.
   */
  public ServerCategoryQuery communicationOptions() {
    startField("communication_options");

    return this;
  }

  /**
   * Options related to the tracking/enforcement of
   */
  public ServerCategoryQuery infractionOptions() {
    startField("infraction_options");

    return this;
  }

  /**
   * Name of the category.
   */
  public ServerCategoryQuery name() {
    startField("name");

    return this;
  }

  /**
   * Options related to the tracking of stats.
   */
  public ServerCategoryQuery trackingOptions() {
    startField("tracking_options");

    return this;
  }
}
