package net.avicus.magma.api.graph.types.base.alert_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class AlertsArguments extends Arguments {

  private final StringBuilder builder;

  public AlertsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public AlertsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public AlertsArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }

  public AlertsArguments name(String value) {
    if (value != null) {
      startArgument("name");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }
}
