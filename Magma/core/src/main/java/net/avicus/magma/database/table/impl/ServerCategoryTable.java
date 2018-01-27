package net.avicus.magma.database.table.impl;

import java.util.Optional;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.ServerCategory;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;

public class ServerCategoryTable extends Table<ServerCategory> {

  public ServerCategoryTable(Database database, String name, Class<ServerCategory> model) {
    super(database, name, model);
  }

  public Optional<ServerCategory> fromServer(Server server) {
    if (server == null || server.getServerCategoryId() == 0) {
      return Optional.empty();
    }

    return Optional
        .ofNullable(select().where("id", server.getServerCategoryId()).execute().first());
  }
}
