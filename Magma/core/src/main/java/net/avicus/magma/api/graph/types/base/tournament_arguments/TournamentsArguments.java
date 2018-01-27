package net.avicus.magma.api.graph.types.base.tournament_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class TournamentsArguments extends Arguments {

  private final StringBuilder builder;

  public TournamentsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public TournamentsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public TournamentsArguments name(String value) {
    if (value != null) {
      startArgument("name");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public TournamentsArguments slug(String value) {
    if (value != null) {
      startArgument("slug");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }
}
