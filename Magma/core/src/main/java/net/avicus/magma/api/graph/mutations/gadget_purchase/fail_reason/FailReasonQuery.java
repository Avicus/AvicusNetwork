package net.avicus.magma.api.graph.mutations.gadget_purchase.fail_reason;

import com.shopify.graphql.support.Query;

public class FailReasonQuery extends Query<FailReasonQuery> {

  public FailReasonQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * If the purchase failed due to a lack of currency.
   */
  public FailReasonQuery money() {
    startField("money");

    return this;
  }

  /**
   * If the purchase failed due to a lack of rank.
   */
  public FailReasonQuery rank() {
    startField("rank");

    return this;
  }
}
