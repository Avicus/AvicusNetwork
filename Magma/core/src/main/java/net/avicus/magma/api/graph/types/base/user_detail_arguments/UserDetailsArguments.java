package net.avicus.magma.api.graph.types.base.user_detail_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class UserDetailsArguments extends Arguments {

  private final StringBuilder builder;

  public UserDetailsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public UserDetailsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public UserDetailsArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }

  public UserDetailsArguments emailStatus(Integer value) {
    if (value != null) {
      startArgument("email_status");
      builder().append(value);
    }
    return this;
  }

  public UserDetailsArguments coverArt(String value) {
    if (value != null) {
      startArgument("cover_art");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public UserDetailsArguments gender(String value) {
    if (value != null) {
      startArgument("gender");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }
}
