package net.avicus.magma.database.table.impl;

import com.google.gson.JsonObject;
import java.util.List;
import net.avicus.magma.database.model.impl.BackpackGadget;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;

public class BackpackGadgetTable extends Table<BackpackGadget> {

  public BackpackGadgetTable(Database database, String name, Class<BackpackGadget> model) {
    super(database, name, model);
  }

  public List<BackpackGadget> findByUser(int userId) {
    return select().where("user_id", userId).execute();
  }

  public void delete(int id) {
    delete().where("id", id).execute();
  }

  public void updateContext(int id, JsonObject context) {
    update().set("context", context.toString()).where("id", id).execute();
  }
}
