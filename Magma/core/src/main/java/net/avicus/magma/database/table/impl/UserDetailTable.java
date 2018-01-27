package net.avicus.magma.database.table.impl;

import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.database.model.impl.UserDetail;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;

public class UserDetailTable extends Table<UserDetail> {

  public UserDetailTable(Database database, String name, Class<UserDetail> model) {
    super(database, name, model);
  }

  public UserDetail findByUser(User user) {
    return select().where("id", user.getId()).execute().first();
  }
}
