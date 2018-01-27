package net.avicus.magma.database.table.impl;

import java.util.List;
import java.util.Optional;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.ModelList;
import net.avicus.quest.model.Table;

public class ServerTable extends Table<Server> {

  public ServerTable(Database database, String name, Class<Server> model) {
    super(database, name, model);
  }

  public Optional<Server> findByName(String name) {
    List<Server> list = select().where("name", name).execute();
    if (list.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(list.get(0));
  }

  public Optional<Server> findById(int id) {
    ModelList<Server> list = select().where("id", id).limit(1).execute();
    if (list.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(list.first());
  }

  public List<Server> findByServerGroup(int groupId) {
    return select().where("server_group_id", groupId).execute();
  }

  public List<Server> findByServerCategory(int categoryId) {
    return select().where("server_category_id", categoryId).execute();
  }
}
