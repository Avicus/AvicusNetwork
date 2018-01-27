package net.avicus.magma.api.graph.types.base.membership_arguments;

import com.shopify.graphql.support.Arguments;

public class MembershipsArguments extends Arguments {

  private final StringBuilder builder;

  public MembershipsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public MembershipsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public MembershipsArguments rankId(Integer value) {
    if (value != null) {
      startArgument("rank_id");
      builder().append(value);
    }
    return this;
  }

  public MembershipsArguments memberId(Integer value) {
    if (value != null) {
      startArgument("member_id");
      builder().append(value);
    }
    return this;
  }

  public MembershipsArguments isPurchased(Boolean value) {
    if (value != null) {
      startArgument("is_purchased");
      builder().append(value);
    }
    return this;
  }
}
