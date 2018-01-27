package net.avicus.magma.database.table.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import net.avicus.magma.database.model.impl.Punishment;
import net.avicus.magma.database.model.impl.User;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.ModelList;
import net.avicus.quest.model.Table;
import net.avicus.quest.query.Operator;

public class PunishmentTable extends Table<Punishment> {

  public PunishmentTable(Database database, String name, Class<Punishment> model) {
    super(database, name, model);
  }

  public ModelList<Punishment> findAfter(Date date) {
    return select().where("date", date, Operator.GREATER).execute();
  }

  public List<Punishment> findByUser(User user) {
    return select().where("user_id", user.getId()).execute();
  }

  public Optional<Punishment> findById(int id) {
    List<Punishment> list = select().where("id", id).limit(1).execute();
    if (list.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(list.get(0));
  }
}
