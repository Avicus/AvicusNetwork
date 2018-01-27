package net.avicus.magma.database.table.impl;

import net.avicus.magma.database.model.impl.PrestigeLevel;
import net.avicus.magma.database.model.impl.PrestigeSeason;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;

public class PrestigeLevelTable extends Table<PrestigeLevel> {

  public PrestigeLevelTable(Database database, String name, Class<PrestigeLevel> model) {
    super(database, name, model);
  }

  public PrestigeLevel currentLevel(int userId, PrestigeSeason season) {
    return select().where("user_id", userId).where("season_id", season.getId()).order("level")
        .execute().last();
  }

  public PrestigeLevel getLatest() {
    return select().execute().last();
  }
}
