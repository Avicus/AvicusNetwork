package net.avicus.magma.api.graph.types.base.server_group_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class ServerGroupsArguments extends Arguments {

  private final StringBuilder builder;

  public ServerGroupsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public ServerGroupsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public ServerGroupsArguments name(String value) {
    if (value != null) {
      startArgument("name");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public ServerGroupsArguments slug(String value) {
    if (value != null) {
      startArgument("slug");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }
}
