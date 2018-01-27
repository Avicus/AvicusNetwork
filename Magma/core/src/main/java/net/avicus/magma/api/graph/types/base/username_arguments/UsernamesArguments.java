package net.avicus.magma.api.graph.types.base.username_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class UsernamesArguments extends Arguments {

  private final StringBuilder builder;

  public UsernamesArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public UsernamesArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public UsernamesArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }

  public UsernamesArguments username(String value) {
    if (value != null) {
      startArgument("username");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }
}
