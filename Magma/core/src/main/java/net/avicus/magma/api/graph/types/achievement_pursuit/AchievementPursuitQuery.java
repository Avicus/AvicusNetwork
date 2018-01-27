package net.avicus.magma.api.graph.types.achievement_pursuit;

import com.shopify.graphql.support.Query;

public class AchievementPursuitQuery extends Query<AchievementPursuitQuery> {

  public AchievementPursuitQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Slug of the achievement used in plugins to protect against name changes.
   */
  public AchievementPursuitQuery slug() {
    startField("slug");

    return this;
  }

  /**
   * ID of the user currently in pursuit of the achievement.
   */
  public AchievementPursuitQuery userId() {
    startField("user_id");

    return this;
  }
}
