package net.avicus.magma.api.graph.types.base.server_booster_arguments;

import com.shopify.graphql.support.Arguments;

public class ServerBoostersArguments extends Arguments {

  private final StringBuilder builder;

  public ServerBoostersArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public ServerBoostersArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public ServerBoostersArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }

  public ServerBoostersArguments serverId(Integer value) {
    if (value != null) {
      startArgument("server_id");
      builder().append(value);
    }
    return this;
  }
}
