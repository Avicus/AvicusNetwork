package net.avicus.magma.database.table.impl;

import net.avicus.magma.database.model.impl.Discussion;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;

public class DiscussionTable extends Table<Discussion> {

  public DiscussionTable(Database database, String name, Class<Discussion> model) {
    super(database, name, model);
  }

  public Discussion getLatest(int catId) {
    return select().where("category_id", catId).order("created_at", "DESC").execute().first();
  }
}
