package net.avicus.magma.api.graph.types.base.backpack_gadget_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class BackpackGadgetsArguments extends Arguments {

  private final StringBuilder builder;

  public BackpackGadgetsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public BackpackGadgetsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public BackpackGadgetsArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }

  public BackpackGadgetsArguments gadgetType(String value) {
    if (value != null) {
      startArgument("gadget_type");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public BackpackGadgetsArguments oldId(Integer value) {
    if (value != null) {
      startArgument("old_id");
      builder().append(value);
    }
    return this;
  }
}
