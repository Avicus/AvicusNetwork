package net.avicus.magma.api.graph.types.base.experience_transaction_arguments;

import com.shopify.graphql.support.Arguments;

public class ExperienceTransactionsArguments extends Arguments {

  private final StringBuilder builder;

  public ExperienceTransactionsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public ExperienceTransactionsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public ExperienceTransactionsArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }

  public ExperienceTransactionsArguments seasonId(Integer value) {
    if (value != null) {
      startArgument("season_id");
      builder().append(value);
    }
    return this;
  }
}
