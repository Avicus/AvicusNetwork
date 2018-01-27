package net.avicus.magma.api.graph.types.base.registration_arguments;

import com.shopify.graphql.support.Arguments;

public class RegistrationsArguments extends Arguments {

  private final StringBuilder builder;

  public RegistrationsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public RegistrationsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public RegistrationsArguments tournamentId(Integer value) {
    if (value != null) {
      startArgument("tournament_id");
      builder().append(value);
    }
    return this;
  }

  public RegistrationsArguments teamId(Integer value) {
    if (value != null) {
      startArgument("team_id");
      builder().append(value);
    }
    return this;
  }

  public RegistrationsArguments status(Integer value) {
    if (value != null) {
      startArgument("status");
      builder().append(value);
    }
    return this;
  }
}
