package net.avicus.magma.api.graph.mutations.present_find;

import com.shopify.graphql.support.Query;

public class PresentFindQuery extends Query<PresentFindQuery> {

  public PresentFindQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * A unique identifier for the client performing the mutation.
   */
  public PresentFindQuery clientMutationId() {
    startField("clientMutationId");

    return this;
  }

  /**
   * Message to be displayed to the player.
   */
  public PresentFindQuery message() {
    startField("message");

    return this;
  }

  /**
   * If the present was successfully marked as found.
   */
  public PresentFindQuery success() {
    startField("success");

    return this;
  }
}
