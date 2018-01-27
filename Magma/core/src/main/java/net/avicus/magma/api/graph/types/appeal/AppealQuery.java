package net.avicus.magma.api.graph.types.appeal;

import com.shopify.graphql.support.Query;

public class AppealQuery extends Query<AppealQuery> {

  public AppealQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * If the punishment attached to this appeal has been appealed.
   */
  public AppealQuery appealed() {
    startField("appealed");

    return this;
  }

  /**
   * Date when this appeal was created.
   */
  public AppealQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * If the appeal has been escalated, allowing only higher staff to comment.
   */
  public AppealQuery escalated() {
    startField("escalated");

    return this;
  }

  /**
   * If the appeal is locked from comments.
   */
  public AppealQuery locked() {
    startField("locked");

    return this;
  }

  /**
   * If the appeal is open for comments.
   */
  public AppealQuery open() {
    startField("open");

    return this;
  }

  /**
   * ID of the punishment that this appeal is for.
   */
  public AppealQuery punishmentId() {
    startField("punishment_id");

    return this;
  }

  /**
   * Date when this appeal was last updated.
   */
  public AppealQuery updatedAt() {
    startField("updated_at");

    return this;
  }

  /**
   * ID of the user who started the appeal.
   */
  public AppealQuery userId() {
    startField("user_id");

    return this;
  }
}
