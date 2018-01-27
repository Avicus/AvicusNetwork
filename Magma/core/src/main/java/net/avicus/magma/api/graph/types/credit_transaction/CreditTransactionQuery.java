package net.avicus.magma.api.graph.types.credit_transaction;

import com.shopify.graphql.support.Query;

public class CreditTransactionQuery extends Query<CreditTransactionQuery> {

  public CreditTransactionQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Number of credits this transaction should represent.
   */
  public CreditTransactionQuery amount() {
    startField("amount");

    return this;
  }

  /**
   * Date when this credittransaction was created.
   */
  public CreditTransactionQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * ID of the user who the credit(s) in this transaction are rewarded to.
   */
  public CreditTransactionQuery userId() {
    startField("user_id");

    return this;
  }

  /**
   * Amount the base credit value was multiplied by. The amount represented by this object already
   * reflects this operation.
   */
  public CreditTransactionQuery weight() {
    startField("weight");

    return this;
  }
}
