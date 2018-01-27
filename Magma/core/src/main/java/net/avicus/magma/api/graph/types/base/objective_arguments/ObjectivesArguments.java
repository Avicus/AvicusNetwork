package net.avicus.magma.api.graph.types.base.objective_arguments;

import com.shopify.graphql.support.Arguments;

public class ObjectivesArguments extends Arguments {

  private final StringBuilder builder;

  public ObjectivesArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public ObjectivesArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public ObjectivesArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }

  public ObjectivesArguments objectiveId(Integer value) {
    if (value != null) {
      startArgument("objective_id");
      builder().append(value);
    }
    return this;
  }

  public ObjectivesArguments hidden(Boolean value) {
    if (value != null) {
      startArgument("hidden");
      builder().append(value);
    }
    return this;
  }
}
