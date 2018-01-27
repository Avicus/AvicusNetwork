package net.avicus.magma.api.graph.types.server;

import com.shopify.graphql.support.Query;

public class ServerQuery extends Query<ServerQuery> {

  public ServerQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Date when this server was created.
   */
  public ServerQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * Host of the box which this server is hosted on.
   */
  public ServerQuery host() {
    startField("host");

    return this;
  }

  /**
   * Name of the server.
   */
  public ServerQuery name() {
    startField("name");

    return this;
  }

  /**
   * If the server can only be joined by users with the correct permission.
   */
  public ServerQuery permissible() {
    startField("permissible");

    return this;
  }

  /**
   * Port of the server.
   */
  public ServerQuery port() {
    startField("port");

    return this;
  }

  /**
   * ID of the server category this server belongs to.
   */
  public ServerQuery serverCategoryId() {
    startField("server_category_id");

    return this;
  }

  /**
   * ID of the server group this server belongs to.
   */
  public ServerQuery serverGroupId() {
    startField("server_group_id");

    return this;
  }

  /**
   * Date when this server was last updated.
   */
  public ServerQuery updatedAt() {
    startField("updated_at");

    return this;
  }
}
