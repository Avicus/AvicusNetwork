package net.avicus.magma.database.table.impl;

import net.avicus.magma.database.model.impl.ObjectiveCompletion;
import net.avicus.magma.database.model.impl.ObjectiveType;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;
import net.avicus.quest.query.Filter;
import net.avicus.quest.query.RowList;

public class ObjectiveTable extends Table<ObjectiveCompletion> {

  public ObjectiveTable(Database database, String name, Class<ObjectiveCompletion> model) {
    super(database, name, model);
  }

  public void hideStats(int userId) {
    Filter filter = new Filter("user_id", userId);
    update().set("hidden", true).where(filter).execute();
  }

  public int getTypeCount(int userId, ObjectiveType type) {
    Filter filter = new Filter("user_id", userId);
    filter.and("objective_id", type.getId());

    RowList list = getDatabase().select(getName()).columns("COUNT(*)").where(filter).execute();
    try {
      return ((Number) list.first().getMap().values().stream().findAny().orElse(0)).intValue();
    } catch (Exception e) {
      return 0;
    }
  }
}
