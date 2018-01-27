package net.avicus.magma.api.graph.mutations.gadget_update;

import com.shopify.graphql.support.Query;

public class GadgetUpdateQuery extends Query<GadgetUpdateQuery> {

  public GadgetUpdateQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * A unique identifier for the client performing the mutation.
   */
  public GadgetUpdateQuery clientMutationId() {
    startField("clientMutationId");

    return this;
  }

  /**
   * If the gadget should be removed from the user's backpack.
   */
  public GadgetUpdateQuery remove() {
    startField("remove");

    return this;
  }

  /**
   * Amount of usages remaining AFTER the current use.
   */
  public GadgetUpdateQuery usagesRemaining() {
    startField("usages_remaining");

    return this;
  }
}
