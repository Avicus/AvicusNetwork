package net.avicus.magma.api.graph.types.base.credit_transaction_arguments;

import com.shopify.graphql.support.Arguments;

public class CreditTransactionsArguments extends Arguments {

  private final StringBuilder builder;

  public CreditTransactionsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public CreditTransactionsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public CreditTransactionsArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }
}
