package net.avicus.magma.api.graph.types.base.team_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class TeamsArguments extends Arguments {

  private final StringBuilder builder;

  public TeamsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public TeamsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public TeamsArguments title(String value) {
    if (value != null) {
      startArgument("title");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public TeamsArguments tag(String value) {
    if (value != null) {
      startArgument("tag");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }
}
