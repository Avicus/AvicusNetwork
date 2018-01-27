package net.avicus.magma.api.graph.types.present_finder;

import com.shopify.graphql.support.Query;

public class PresentFinderQuery extends Query<PresentFinderQuery> {

  public PresentFinderQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * ID of the present that the user found.
   */
  public PresentFinderQuery presentId() {
    startField("present_id");

    return this;
  }

  /**
   * ID of the user that found the present.
   */
  public PresentFinderQuery userId() {
    startField("user_id");

    return this;
  }
}
