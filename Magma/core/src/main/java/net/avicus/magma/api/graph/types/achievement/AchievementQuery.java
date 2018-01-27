package net.avicus.magma.api.graph.types.achievement;

import com.shopify.graphql.support.Query;

public class AchievementQuery extends Query<AchievementQuery> {

  public AchievementQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Description of the achievement used in the UI.
   */
  public AchievementQuery description() {
    startField("description");

    return this;
  }

  /**
   * Name of the achievement used in the UI.
   */
  public AchievementQuery name() {
    startField("name");

    return this;
  }

  /**
   * Slug of the achievement used in plugins to protect against name changes.
   */
  public AchievementQuery slug() {
    startField("slug");

    return this;
  }
}
