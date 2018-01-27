package net.avicus.magma.api.graph.types.base.achievement_receiver_arguments;

import com.shopify.graphql.support.Arguments;

public class AchievementReceiversArguments extends Arguments {

  private final StringBuilder builder;

  public AchievementReceiversArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public AchievementReceiversArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public AchievementReceiversArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }

  public AchievementReceiversArguments achievementId(Integer value) {
    if (value != null) {
      startArgument("achievement_id");
      builder().append(value);
    }
    return this;
  }
}
