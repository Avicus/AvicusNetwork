package net.avicus.magma.api.graph.types.base.session_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class SessionsArguments extends Arguments {

  private final StringBuilder builder;

  public SessionsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public SessionsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public SessionsArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }

  public SessionsArguments ip(String value) {
    if (value != null) {
      startArgument("ip");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public SessionsArguments serverId(Integer value) {
    if (value != null) {
      startArgument("server_id");
      builder().append(value);
    }
    return this;
  }

  public SessionsArguments isActive(Boolean value) {
    if (value != null) {
      startArgument("is_active");
      builder().append(value);
    }
    return this;
  }

  public SessionsArguments graceful(Boolean value) {
    if (value != null) {
      startArgument("graceful");
      builder().append(value);
    }
    return this;
  }
}
