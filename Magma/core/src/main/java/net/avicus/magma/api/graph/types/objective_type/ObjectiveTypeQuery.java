package net.avicus.magma.api.graph.types.objective_type;

import com.shopify.graphql.support.Query;

public class ObjectiveTypeQuery extends Query<ObjectiveTypeQuery> {

  public ObjectiveTypeQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Name of the objective type.
   */
  public ObjectiveTypeQuery name() {
    startField("name");

    return this;
  }
}
