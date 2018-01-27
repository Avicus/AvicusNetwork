package net.avicus.magma.api.graph.types.base.present_finder_arguments;

import com.shopify.graphql.support.Arguments;

public class PresentFindersArguments extends Arguments {

  private final StringBuilder builder;

  public PresentFindersArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public PresentFindersArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public PresentFindersArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }

  public PresentFindersArguments presentId(Integer value) {
    if (value != null) {
      startArgument("present_id");
      builder().append(value);
    }
    return this;
  }
}
