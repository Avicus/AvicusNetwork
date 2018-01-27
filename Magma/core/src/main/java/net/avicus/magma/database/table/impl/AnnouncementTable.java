package net.avicus.magma.database.table.impl;

import java.util.List;
import net.avicus.magma.database.model.impl.Announcement;
import net.avicus.magma.database.model.impl.Announcement.Type;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;

public class AnnouncementTable extends Table<Announcement> {

  public AnnouncementTable(Database database, String name, Class<Announcement> model) {
    super(database, name, model);
  }

  public List<Announcement> findByType(Type type) {
    return select().where(type.columnName(), true).where("enabled", true).execute();
  }
}
