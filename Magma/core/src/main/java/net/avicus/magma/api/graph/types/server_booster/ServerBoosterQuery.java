package net.avicus.magma.api.graph.types.server_booster;

import com.shopify.graphql.support.Query;

public class ServerBoosterQuery extends Query<ServerBoosterQuery> {

  public ServerBoosterQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Date when this serverbooster was created.
   */
  public ServerBoosterQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * When this booster ends.
   */
  public ServerBoosterQuery expiresAt() {
    startField("expires_at");

    return this;
  }

  /**
   * Amount XP should be multiplied by while this booster is active.
   */
  public ServerBoosterQuery multiplier() {
    startField("multiplier");

    return this;
  }

  /**
   * Server that the booster applies to.
   */
  public ServerBoosterQuery serverId() {
    startField("server_id");

    return this;
  }

  /**
   * When this booster begins.
   */
  public ServerBoosterQuery startsAt() {
    startField("starts_at");

    return this;
  }

  /**
   * Date when this serverbooster was last updated.
   */
  public ServerBoosterQuery updatedAt() {
    startField("updated_at");

    return this;
  }

  /**
   * ID of the user who owns the booster.
   */
  public ServerBoosterQuery userId() {
    startField("user_id");

    return this;
  }
}
