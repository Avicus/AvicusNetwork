package net.avicus.magma.api.graph.types.base.punishment_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class PunishmentsArguments extends Arguments {

  private final StringBuilder builder;

  public PunishmentsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public PunishmentsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public PunishmentsArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }

  public PunishmentsArguments staffId(Integer value) {
    if (value != null) {
      startArgument("staff_id");
      builder().append(value);
    }
    return this;
  }

  public PunishmentsArguments type(String value) {
    if (value != null) {
      startArgument("type");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public PunishmentsArguments serverId(Integer value) {
    if (value != null) {
      startArgument("server_id");
      builder().append(value);
    }
    return this;
  }

  public PunishmentsArguments appealed(Integer value) {
    if (value != null) {
      startArgument("appealed");
      builder().append(value);
    }
    return this;
  }

  public PunishmentsArguments silent(Boolean value) {
    if (value != null) {
      startArgument("silent");
      builder().append(value);
    }
    return this;
  }
}
