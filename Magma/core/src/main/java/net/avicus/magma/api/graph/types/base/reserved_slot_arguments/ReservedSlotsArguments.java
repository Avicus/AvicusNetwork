package net.avicus.magma.api.graph.types.base.reserved_slot_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class ReservedSlotsArguments extends Arguments {

  private final StringBuilder builder;

  public ReservedSlotsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public ReservedSlotsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public ReservedSlotsArguments teamId(Integer value) {
    if (value != null) {
      startArgument("team_id");
      builder().append(value);
    }
    return this;
  }

  public ReservedSlotsArguments server(String value) {
    if (value != null) {
      startArgument("server");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public ReservedSlotsArguments reservee(Integer value) {
    if (value != null) {
      startArgument("reservee");
      builder().append(value);
    }
    return this;
  }
}
