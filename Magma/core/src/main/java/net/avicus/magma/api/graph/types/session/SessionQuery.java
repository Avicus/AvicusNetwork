package net.avicus.magma.api.graph.types.session;

import com.shopify.graphql.support.Query;

public class SessionQuery extends Query<SessionQuery> {

  public SessionQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Date when this session was created.
   */
  public SessionQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * How long (in seconds) that the session lasted.
   */
  public SessionQuery duration() {
    startField("duration");

    return this;
  }

  /**
   * If the session ended without a crash.
   */
  public SessionQuery graceful() {
    startField("graceful");

    return this;
  }

  /**
   * IP of the user during the session
   */
  public SessionQuery ip() {
    startField("ip");

    return this;
  }

  /**
   * If the session is still ongoing.
   */
  public SessionQuery isActive() {
    startField("is_active");

    return this;
  }

  /**
   * ID of the server that the session happened on.
   */
  public SessionQuery serverId() {
    startField("server_id");

    return this;
  }

  /**
   * Date when this session was last updated.
   */
  public SessionQuery updatedAt() {
    startField("updated_at");

    return this;
  }

  /**
   * ID of the user that was on the server.
   */
  public SessionQuery userId() {
    startField("user_id");

    return this;
  }
}
