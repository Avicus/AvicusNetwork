package net.avicus.magma.database.table.impl;

import net.avicus.magma.database.model.impl.LeaderboardEntry;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;

/**
 * Leaderboards!
 * <p>
 * Note: This is not name LeaderboardEntryTable, because that's too long,
 * and LeaderboardTable just makes sense.
 */
public class LeaderboardTable extends Table<LeaderboardEntry> {

  public LeaderboardTable(Database database, String name, Class<LeaderboardEntry> model) {
    super(database, name, model);
  }
}
