package net.avicus.magma.api.graph.types.base.achievement_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class AchievementsArguments extends Arguments {

  private final StringBuilder builder;

  public AchievementsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public AchievementsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public AchievementsArguments slug(String value) {
    if (value != null) {
      startArgument("slug");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public AchievementsArguments name(String value) {
    if (value != null) {
      startArgument("name");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }
}
