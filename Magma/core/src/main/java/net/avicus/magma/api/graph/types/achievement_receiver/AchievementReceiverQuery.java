package net.avicus.magma.api.graph.types.achievement_receiver;

import com.shopify.graphql.support.Query;

public class AchievementReceiverQuery extends Query<AchievementReceiverQuery> {

  public AchievementReceiverQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * ID of the achievement that the user is receiving.
   */
  public AchievementReceiverQuery achievementId() {
    startField("achievement_id");

    return this;
  }

  /**
   * ID of the user that received the Achievement
   */
  public AchievementReceiverQuery userId() {
    startField("user_id");

    return this;
  }
}
