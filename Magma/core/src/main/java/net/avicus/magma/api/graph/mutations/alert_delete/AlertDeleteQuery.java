package net.avicus.magma.api.graph.mutations.alert_delete;

import com.shopify.graphql.support.Query;

public class AlertDeleteQuery extends Query<AlertDeleteQuery> {

  public AlertDeleteQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * A unique identifier for the client performing the mutation.
   */
  public AlertDeleteQuery clientMutationId() {
    startField("clientMutationId");

    return this;
  }

  /**
   * If the alert was delete.
   */
  public AlertDeleteQuery success() {
    startField("success");

    return this;
  }
}
