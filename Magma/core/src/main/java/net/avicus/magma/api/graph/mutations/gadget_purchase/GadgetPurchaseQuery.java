package net.avicus.magma.api.graph.mutations.gadget_purchase;

import com.shopify.graphql.support.Query;
import net.avicus.magma.api.graph.mutations.gadget_purchase.fail_reason.FailReasonQuery;
import net.avicus.magma.api.graph.mutations.gadget_purchase.fail_reason.FailReasonQueryDefinition;

public class GadgetPurchaseQuery extends Query<GadgetPurchaseQuery> {

  public GadgetPurchaseQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * A unique identifier for the client performing the mutation.
   */
  public GadgetPurchaseQuery clientMutationId() {
    startField("clientMutationId");

    return this;
  }

  /**
   * Information about why the purchase failed.
   */
  public GadgetPurchaseQuery failReason(FailReasonQueryDefinition queryDef) {
    startField("fail_reason");

    builder().append('{');
    queryDef.define(new FailReasonQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * If the purchase failed.
   */
  public GadgetPurchaseQuery failed() {
    startField("failed");

    return this;
  }
}
