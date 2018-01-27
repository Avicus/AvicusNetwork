package net.avicus.magma.api.graph.types.base.death_arguments;

import com.shopify.graphql.support.Arguments;

public class DeathsArguments extends Arguments {

  private final StringBuilder builder;

  public DeathsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public DeathsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public DeathsArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }

  public DeathsArguments cause(Integer value) {
    if (value != null) {
      startArgument("cause");
      builder().append(value);
    }
    return this;
  }

  public DeathsArguments userHidden(Boolean value) {
    if (value != null) {
      startArgument("user_hidden");
      builder().append(value);
    }
    return this;
  }

  public DeathsArguments causeHidden(Boolean value) {
    if (value != null) {
      startArgument("cause_hidden");
      builder().append(value);
    }
    return this;
  }
}
