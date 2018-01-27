package net.avicus.magma.database.table.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import net.avicus.magma.database.model.impl.ReservedSlot;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.ModelList;
import net.avicus.quest.model.Table;
import net.avicus.quest.query.Filter;
import net.avicus.quest.query.Operator;

public class ReservedSlotTable extends Table<ReservedSlot> {

  public ReservedSlotTable(Database database, String name, Class<ReservedSlot> model) {
    super(database, name, model);
  }

  public Optional<ReservedSlot> findCurrentReservation(String server) {
    Date now = new Date();
    Filter date = new Filter().where("server", server)
        .where("start_at", now, Operator.LESS_OR_EQUAL)
        .and("end_at", now, Operator.GREATER_OR_EQUAL);
    ModelList<ReservedSlot> reservations = select().where(date).limit(1).execute();

    if (reservations.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(reservations.first());
  }

  /**
   * Find reservations where their start_at time is between specific times.
   */
  public List<ReservedSlot> findByStart(Date from, Date to) {
    return select().where("start_at", from, Operator.GREATER_OR_EQUAL)
        .where("start_at", to, Operator.LESS).execute();
  }
}
