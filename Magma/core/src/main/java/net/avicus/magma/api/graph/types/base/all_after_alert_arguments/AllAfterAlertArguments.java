package net.avicus.magma.api.graph.types.base.all_after_alert_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;
import org.joda.time.DateTime;

public class AllAfterAlertArguments extends Arguments {

  private final StringBuilder builder;

  public AllAfterAlertArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public AllAfterAlertArguments createdAt(DateTime value) {
    if (value != null) {
      startArgument("created_at");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }
}
