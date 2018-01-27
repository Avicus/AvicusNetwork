package net.avicus.magma.api.graph.types.base.setting_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class SettingsArguments extends Arguments {

  private final StringBuilder builder;

  public SettingsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public SettingsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public SettingsArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }

  public SettingsArguments key(String value) {
    if (value != null) {
      startArgument("key");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }
}
