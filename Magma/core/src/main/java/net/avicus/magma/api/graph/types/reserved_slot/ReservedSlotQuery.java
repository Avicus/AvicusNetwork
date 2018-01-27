package net.avicus.magma.api.graph.types.reserved_slot;

import com.shopify.graphql.support.Query;

public class ReservedSlotQuery extends Query<ReservedSlotQuery> {

  public ReservedSlotQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Date when this reservedslot was created.
   */
  public ReservedSlotQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * When the reservation ends.
   */
  public ReservedSlotQuery endAt() {
    startField("end_at");

    return this;
  }

  /**
   * ID of the user who made the reservation.
   */
  public ReservedSlotQuery reservee() {
    startField("reservee");

    return this;
  }

  /**
   * Name of the server which is reserved.
   */
  public ReservedSlotQuery server() {
    startField("server");

    return this;
  }

  /**
   * When the reservation starts.
   */
  public ReservedSlotQuery startAt() {
    startField("start_at");

    return this;
  }

  /**
   * ID of the team that owns the server.
   */
  public ReservedSlotQuery teamId() {
    startField("team_id");

    return this;
  }
}
