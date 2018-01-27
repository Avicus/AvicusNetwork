package net.avicus.magma.api.graph.types.base.user_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class UsersArguments extends Arguments {

  private final StringBuilder builder;

  public UsersArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public UsersArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public UsersArguments username(String value) {
    if (value != null) {
      startArgument("username");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public UsersArguments uuid(String value) {
    if (value != null) {
      startArgument("uuid");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public UsersArguments locale(String value) {
    if (value != null) {
      startArgument("locale");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public UsersArguments mcVersion(Integer value) {
    if (value != null) {
      startArgument("mc_version");
      builder().append(value);
    }
    return this;
  }

  public UsersArguments discordId(Integer value) {
    if (value != null) {
      startArgument("discord_id");
      builder().append(value);
    }
    return this;
  }
}
