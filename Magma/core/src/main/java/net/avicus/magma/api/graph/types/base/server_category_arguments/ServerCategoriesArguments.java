package net.avicus.magma.api.graph.types.base.server_category_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class ServerCategoriesArguments extends Arguments {

  private final StringBuilder builder;

  public ServerCategoriesArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public ServerCategoriesArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public ServerCategoriesArguments name(String value) {
    if (value != null) {
      startArgument("name");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }
}
