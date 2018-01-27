package net.avicus.magma.api.graph.types.base.report_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class ReportsArguments extends Arguments {

  private final StringBuilder builder;

  public ReportsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public ReportsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public ReportsArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }

  public ReportsArguments creatorId(Integer value) {
    if (value != null) {
      startArgument("creator_id");
      builder().append(value);
    }
    return this;
  }

  public ReportsArguments server(String value) {
    if (value != null) {
      startArgument("server");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }
}
