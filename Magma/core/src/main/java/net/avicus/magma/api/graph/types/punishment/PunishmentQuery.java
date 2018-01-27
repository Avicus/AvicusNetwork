package net.avicus.magma.api.graph.types.punishment;

import com.shopify.graphql.support.Query;

public class PunishmentQuery extends Query<PunishmentQuery> {

  public PunishmentQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * If this punishment has been appealed.
   */
  public PunishmentQuery appealed() {
    startField("appealed");

    return this;
  }

  /**
   * Date that this punishment was issued.
   */
  public PunishmentQuery date() {
    startField("date");

    return this;
  }

  /**
   * Date when this punishment is set to expire.
   */
  public PunishmentQuery expires() {
    startField("expires");

    return this;
  }

  /**
   * The reason this punishment was issued.
   */
  public PunishmentQuery reason() {
    startField("reason");

    return this;
  }

  /**
   * ID of the server that this punishment was issued on.
   */
  public PunishmentQuery serverId() {
    startField("server_id");

    return this;
  }

  /**
   * If the punishment was displayed in the UI when it was issued.
   */
  public PunishmentQuery silent() {
    startField("silent");

    return this;
  }

  /**
   * User who issued this punishment.
   */
  public PunishmentQuery staffId() {
    startField("staff_id");

    return this;
  }

  /**
   * Type of punishment
   */
  public PunishmentQuery type() {
    startField("type");

    return this;
  }

  /**
   * User who received this punishment.
   */
  public PunishmentQuery userId() {
    startField("user_id");

    return this;
  }
}
