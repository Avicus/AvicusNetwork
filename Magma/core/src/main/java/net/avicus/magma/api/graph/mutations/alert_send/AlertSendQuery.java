package net.avicus.magma.api.graph.mutations.alert_send;

import com.shopify.graphql.support.Query;

public class AlertSendQuery extends Query<AlertSendQuery> {

  public AlertSendQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * A unique identifier for the client performing the mutation.
   */
  public AlertSendQuery clientMutationId() {
    startField("clientMutationId");

    return this;
  }

  /**
   * If the user was alerted.
   */
  public AlertSendQuery success() {
    startField("success");

    return this;
  }
}
