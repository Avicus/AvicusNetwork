package net.avicus.magma.api.graph.types.base.livestream_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class LivestreamsArguments extends Arguments {

  private final StringBuilder builder;

  public LivestreamsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public LivestreamsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public LivestreamsArguments channel(String value) {
    if (value != null) {
      startArgument("channel");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }
}
