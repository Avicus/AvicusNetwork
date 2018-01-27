package net.avicus.magma.api.graph.types.base.team_member_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class TeamMembersArguments extends Arguments {

  private final StringBuilder builder;

  public TeamMembersArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public TeamMembersArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public TeamMembersArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }

  public TeamMembersArguments role(String value) {
    if (value != null) {
      startArgument("role");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public TeamMembersArguments teamId(Integer value) {
    if (value != null) {
      startArgument("team_id");
      builder().append(value);
    }
    return this;
  }

  public TeamMembersArguments accepted(Integer value) {
    if (value != null) {
      startArgument("accepted");
      builder().append(value);
    }
    return this;
  }
}
