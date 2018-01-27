package net.avicus.magma.database.table.impl;

import java.util.Optional;
import net.avicus.magma.database.model.impl.ObjectiveType;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;

public class ObjectiveTypeTable extends Table<ObjectiveType> {

  public ObjectiveTypeTable(Database database, String name, Class<ObjectiveType> model) {
    super(database, name, model);
  }

  public ObjectiveType findOrCreate(String name) {
    Optional<ObjectiveType> search = select().where("name", name).execute().stream().findAny();
    if (search.isPresent()) {
      return search.get();
    }

    ObjectiveType type = new ObjectiveType(name);
    return insert(type).execute();
  }
}
