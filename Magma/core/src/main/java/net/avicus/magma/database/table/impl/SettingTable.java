package net.avicus.magma.database.table.impl;

import java.util.List;
import net.avicus.magma.database.model.impl.Setting;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.ModelList;
import net.avicus.quest.model.Table;

public class SettingTable extends Table<Setting> {

  public SettingTable(Database database, String name, Class<Setting> model) {
    super(database, name, model);
  }

  public List<Setting> findByUser(int userId) {
    return select().where("user_id", userId).execute();
  }

  public void updateOrSet(int userId, String key, String value) {
    ModelList<Setting> list = this.select().where("user_id", userId).where("key", key).execute();
    if (list.isEmpty()) {
      this.insert(new Setting(userId, key, value)).execute();
    } else {
      this.update().set("value", value).where("user_id", userId).where("key", key).execute();
    }
  }
}
