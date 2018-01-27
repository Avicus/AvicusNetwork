package net.avicus.magma.api.graph.types.objective;

import com.shopify.graphql.support.Query;

public class ObjectiveQuery extends Query<ObjectiveQuery> {

  public ObjectiveQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Date when this objective was created.
   */
  public ObjectiveQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * If this completion was hidden by a stats reset gadget.
   */
  public ObjectiveQuery hidden() {
    startField("hidden");

    return this;
  }

  /**
   * ID of the objective which was completed.
   */
  public ObjectiveQuery objectiveId() {
    startField("objective_id");

    return this;
  }

  /**
   * ID of the user who completed the objective.
   */
  public ObjectiveQuery userId() {
    startField("user_id");

    return this;
  }
}
