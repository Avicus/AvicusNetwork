package net.avicus.magma.api.graph.types.server_group;

import com.shopify.graphql.support.Query;

public class ServerGroupQuery extends Query<ServerGroupQuery> {

  public ServerGroupQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Date when this servergroup was created.
   */
  public ServerGroupQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * General data for this group.
   */
  public ServerGroupQuery data() {
    startField("data");

    return this;
  }

  /**
   * Description of the group used in UI.
   */
  public ServerGroupQuery description() {
    startField("description");

    return this;
  }

  /**
   * Icon for the server picker
   */
  public ServerGroupQuery icon() {
    startField("icon");

    return this;
  }

  /**
   * Name of the group.
   */
  public ServerGroupQuery name() {
    startField("name");

    return this;
  }

  /**
   * The slug of group.
   */
  public ServerGroupQuery slug() {
    startField("slug");

    return this;
  }

  /**
   * Date when this servergroup was last updated.
   */
  public ServerGroupQuery updatedAt() {
    startField("updated_at");

    return this;
  }
}
