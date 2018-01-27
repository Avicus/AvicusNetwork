package net.avicus.magma.api.graph.types.base.rank_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class RanksArguments extends Arguments {

  private final StringBuilder builder;

  public RanksArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public RanksArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public RanksArguments name(String value) {
    if (value != null) {
      startArgument("name");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public RanksArguments isStaff(Boolean value) {
    if (value != null) {
      startArgument("is_staff");
      builder().append(value);
    }
    return this;
  }
}
