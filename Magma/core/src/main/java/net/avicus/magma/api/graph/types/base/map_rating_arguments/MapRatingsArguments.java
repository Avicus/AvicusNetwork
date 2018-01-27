package net.avicus.magma.api.graph.types.base.map_rating_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class MapRatingsArguments extends Arguments {

  private final StringBuilder builder;

  public MapRatingsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public MapRatingsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public MapRatingsArguments player(Integer value) {
    if (value != null) {
      startArgument("player");
      builder().append(value);
    }
    return this;
  }

  public MapRatingsArguments mapSlug(String value) {
    if (value != null) {
      startArgument("map_slug");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public MapRatingsArguments mapVersion(String value) {
    if (value != null) {
      startArgument("map_version");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public MapRatingsArguments rating(Integer value) {
    if (value != null) {
      startArgument("rating");
      builder().append(value);
    }
    return this;
  }
}
