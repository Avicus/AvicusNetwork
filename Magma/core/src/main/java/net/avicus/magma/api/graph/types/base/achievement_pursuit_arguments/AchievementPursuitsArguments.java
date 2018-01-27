package net.avicus.magma.api.graph.types.base.achievement_pursuit_arguments;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Query;

public class AchievementPursuitsArguments extends Arguments {

  private final StringBuilder builder;

  public AchievementPursuitsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public AchievementPursuitsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public AchievementPursuitsArguments slug(String value) {
    if (value != null) {
      startArgument("slug");
      Query.appendQuotedString(builder(), value.toString());
    }
    return this;
  }

  public AchievementPursuitsArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }
}
