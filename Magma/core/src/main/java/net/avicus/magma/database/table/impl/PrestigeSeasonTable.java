package net.avicus.magma.database.table.impl;

import java.util.Date;
import java.util.Optional;
import net.avicus.magma.database.model.impl.PrestigeSeason;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.ModelList;
import net.avicus.quest.model.Table;
import net.avicus.quest.query.Filter;
import net.avicus.quest.query.Operator;

public class PrestigeSeasonTable extends Table<PrestigeSeason> {

  public PrestigeSeasonTable(Database database, String name, Class<PrestigeSeason> model) {
    super(database, name, model);
  }

  public Optional<PrestigeSeason> findCurrentSeason() {
    Date now = new Date();
    Filter date = new Filter().where("start_at", now, Operator.LESS_OR_EQUAL)
        .and("end_at", now, Operator.GREATER_OR_EQUAL);
    ModelList<PrestigeSeason> slots = select().where(date).limit(1).execute();

    if (slots.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(slots.first());
  }

  public Optional<PrestigeSeason> findById(int id) {
    ModelList<PrestigeSeason> list = select().where("id", id).limit(1).execute();
    if (list.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(list.first());
  }
}
