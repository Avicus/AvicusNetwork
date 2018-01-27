package net.avicus.magma.database.table.impl;

import java.util.List;
import java.util.Optional;
import net.avicus.magma.database.model.impl.AchievementPursuit;
import net.avicus.magma.database.model.impl.User;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;

public class AchievementPursuitTable extends Table<AchievementPursuit> {

  public AchievementPursuitTable(Database database, String name, Class<AchievementPursuit> model) {
    super(database, name, model);
  }

  public List<AchievementPursuit> findByUser(int userId) {
    return select().where("user_id", userId).execute();
  }

  public boolean isPursuing(String slug, User user) {
    return !select().where("user_id", user.getId()).where("slug", slug).execute().isEmpty();
  }

  public int increment(String slug, User user) {
    int value;
    Optional<AchievementPursuit> existing = Optional
        .ofNullable(select().where("user_id", user.getId()).where("slug", slug).execute().first());
    if (existing.isPresent()) {
      value = existing.get().getProgress() + 1;
      update().where("id", existing.get().getId()).set("progress", value).execute();
    } else {
      insert(new AchievementPursuit(slug, user.getId(), 1)).execute();
      value = 1;
    }

    return value;
  }
}
