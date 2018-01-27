package net.avicus.magma.database.table.impl;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import net.avicus.magma.database.model.impl.Vote;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;
import net.avicus.quest.query.Operator;

public class VoteTable extends Table<Vote> {

  public VoteTable(Database database, String name,
      Class<Vote> model) {
    super(database, name, model);
  }

  public int votesToday(int userId) {
    // today
    Calendar date = new GregorianCalendar();
    // reset hour, minutes, seconds and millis
    date.set(Calendar.HOUR_OF_DAY, 0);
    date.set(Calendar.MINUTE, 0);
    date.set(Calendar.SECOND, 0);
    date.set(Calendar.MILLISECOND, 0);
    return select().where("user_id", userId)
        .where("cast_at", date.getTime(), Operator.GREATER_OR_EQUAL).execute().size();
  }

  public List<Vote> findByUser(int userId) {
    return select().where("user_id", userId).execute();
  }
}
