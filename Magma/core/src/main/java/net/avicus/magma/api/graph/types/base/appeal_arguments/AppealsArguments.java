package net.avicus.magma.api.graph.types.base.appeal_arguments;

import com.shopify.graphql.support.Arguments;

public class AppealsArguments extends Arguments {

  private final StringBuilder builder;

  public AppealsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public AppealsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public AppealsArguments punishmentId(Integer value) {
    if (value != null) {
      startArgument("punishment_id");
      builder().append(value);
    }
    return this;
  }

  public AppealsArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }

  public AppealsArguments open(Boolean value) {
    if (value != null) {
      startArgument("open");
      builder().append(value);
    }
    return this;
  }

  public AppealsArguments locked(Boolean value) {
    if (value != null) {
      startArgument("locked");
      builder().append(value);
    }
    return this;
  }

  public AppealsArguments appealed(Boolean value) {
    if (value != null) {
      startArgument("appealed");
      builder().append(value);
    }
    return this;
  }

  public AppealsArguments escalated(Boolean value) {
    if (value != null) {
      startArgument("escalated");
      builder().append(value);
    }
    return this;
  }
}
