package net.avicus.magma.api.graph.types.base.server_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class ServersArguments extends Arguments {

  private final StringBuilder builder;

  public ServersArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public ServersArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public ServersArguments name(String value) {
    if (value != null) {
      startArgument("name");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public ServersArguments host(String value) {
    if (value != null) {
      startArgument("host");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public ServersArguments serverGroupId(Integer value) {
    if (value != null) {
      startArgument("server_group_id");
      builder().append(value);
    }
    return this;
  }

  public ServersArguments serverCategoryId(Integer value) {
    if (value != null) {
      startArgument("server_category_id");
      builder().append(value);
    }
    return this;
  }
}
