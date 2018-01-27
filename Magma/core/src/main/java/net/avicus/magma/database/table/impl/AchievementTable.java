package net.avicus.magma.database.table.impl;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.avicus.magma.database.model.impl.Achievement;
import net.avicus.magma.database.model.impl.User;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;
import net.avicus.quest.query.Operator;

public class AchievementTable extends Table<Achievement> {

  public AchievementTable(Database database, String name, Class<Achievement> model) {
    super(database, name, model);
  }

  public List<Achievement> findByUser(int userId, AchievementReceiverTable table) {
    return table.select().where("user_id", userId).execute().stream()
        .flatMap(r -> select().where("id", r.getAchievementId()).limit(1).execute().stream())
        .collect(
            Collectors.toList());
  }

  public boolean hasHigher(Achievement achievement, User user, AchievementReceiverTable table) {
    for (Achievement ac : findByUser(user.getId(), table)) {
      if (ac.getRawSlug().equals(achievement.getRawSlug()) && ac.getNum() > achievement.getNum()) {
        return true;
      }
    }
    return false;
  }

  public List<Achievement> softFind(String slug) {
    slug = slug.toLowerCase().replaceAll(" ", "-");

    return select().where("slug", "%" + slug + "%", Operator.LIKE).execute();
  }

  public Achievement getOrCreate(String slug) {
    slug = slug.toLowerCase().replaceAll(" ", "-");
    Achievement found = select().where("slug", slug).execute().first();
    if (found == null) {
      found = insert(new Achievement(slug)).execute();
      Logger.getGlobal().info("Inserting new achievement: " + slug);
    }
    return found;
  }
}
