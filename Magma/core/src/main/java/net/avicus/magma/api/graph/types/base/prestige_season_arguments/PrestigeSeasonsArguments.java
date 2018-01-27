package net.avicus.magma.api.graph.types.base.prestige_season_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class PrestigeSeasonsArguments extends Arguments {

  private final StringBuilder builder;

  public PrestigeSeasonsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public PrestigeSeasonsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public PrestigeSeasonsArguments name(String value) {
    if (value != null) {
      startArgument("name");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }
}
