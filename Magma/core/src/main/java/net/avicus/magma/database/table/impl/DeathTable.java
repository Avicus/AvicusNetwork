package net.avicus.magma.database.table.impl;

import java.util.Date;
import net.avicus.magma.database.model.impl.Death;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;
import net.avicus.quest.query.Filter;
import net.avicus.quest.query.Operator;
import net.avicus.quest.query.RowList;

public class DeathTable extends Table<Death> {

  public DeathTable(Database database, String name, Class<Death> model) {
    super(database, name, model);
  }

  public void hideStats(int userId) {
    Filter filter = new Filter("cause", userId);
    filter.or("user_id", userId);
    update().set("user_hidden", true).set("cause_hidden", true).where(filter).execute();
  }

  public int kills(int userId, Date from, Date to) {
    Filter filter = new Filter("cause", userId);
    filter.and("created_at", from, Operator.GREATER_OR_EQUAL);
    filter.and("created_at", to, Operator.LESS_OR_EQUAL);
    filter.and("cause_hidden", false);

    RowList list = getDatabase().select(getName()).columns("COUNT(*)").where(filter).execute();
    try {
      return ((Number) list.first().getMap().values().stream().findAny().orElse(0)).intValue();
    } catch (Exception e) {
      return 0;
    }
  }

  public int kills(int userId) {
    Filter filter = new Filter("cause", userId);
    filter.and("cause_hidden", false);

    RowList list = getDatabase().select(getName()).columns("COUNT(*)").where(filter).execute();
    try {
      return ((Number) list.first().getMap().values().stream().findAny().orElse(0)).intValue();
    } catch (Exception e) {
      return 0;
    }
  }

  public int deaths(int userId, Date from, Date to) {
    Filter filter = new Filter("user_id", userId);
    filter.and("created_at", from, Operator.GREATER_OR_EQUAL);
    filter.and("created_at", to, Operator.LESS_OR_EQUAL);
    filter.and("user_hidden", false);

    RowList list = getDatabase().select(getName()).columns("COUNT(*)").where(filter).execute();

    try {
      return ((Number) list.first().getMap().values().stream().findAny().orElse(0)).intValue();
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }
}
