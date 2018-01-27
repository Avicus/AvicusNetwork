package net.avicus.magma.api.graph.types.alert;

import com.shopify.graphql.support.Query;

public class AlertQuery extends Query<AlertQuery> {

  public AlertQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * When the alert was created.
   */
  public AlertQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * The ID of the alert
   */
  public AlertQuery id() {
    startField("id");

    return this;
  }

  /**
   * The message which is displayed to the user.
   */
  public AlertQuery message() {
    startField("message");

    return this;
  }

  /**
   * Unique name of the Alert.
   */
  public AlertQuery name() {
    startField("name");

    return this;
  }

  /**
   * If the alert has been read.
   */
  public AlertQuery seen() {
    startField("seen");

    return this;
  }

  /**
   * URL that this alert will direct to when clicked.
   */
  public AlertQuery url() {
    startField("url");

    return this;
  }

  /**
   * ID of the user that this alert is for.
   */
  public AlertQuery userId() {
    startField("user_id");

    return this;
  }
}
