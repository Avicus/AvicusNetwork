package net.avicus.magma.api.graph.types.base.friend_arguments;

import com.shopify.graphql.support.Arguments;

public class FriendsArguments extends Arguments {

  private final StringBuilder builder;

  public FriendsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public FriendsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public FriendsArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }

  public FriendsArguments friendId(Integer value) {
    if (value != null) {
      startArgument("friend_id");
      builder().append(value);
    }
    return this;
  }

  public FriendsArguments accepted(Integer value) {
    if (value != null) {
      startArgument("accepted");
      builder().append(value);
    }
    return this;
  }
}
