package net.avicus.magma.database.table.impl;

import java.util.List;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.database.model.impl.Username;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;

public class UsernameTable extends Table<Username> {

  public UsernameTable(Database database, String name, Class<Username> model) {
    super(database, name, model);
  }

  public List<Username> findByUser(User user) {
    return select().where("user_id", user.getId()).order("created_at", "DESC").execute();
  }
}
