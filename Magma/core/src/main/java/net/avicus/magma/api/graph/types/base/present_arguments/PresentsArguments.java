package net.avicus.magma.api.graph.types.base.present_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class PresentsArguments extends Arguments {

  private final StringBuilder builder;

  public PresentsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public PresentsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public PresentsArguments slug(String value) {
    if (value != null) {
      startArgument("slug");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public PresentsArguments humanName(String value) {
    if (value != null) {
      startArgument("human_name");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public PresentsArguments humanLocation(String value) {
    if (value != null) {
      startArgument("human_location");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }
}
