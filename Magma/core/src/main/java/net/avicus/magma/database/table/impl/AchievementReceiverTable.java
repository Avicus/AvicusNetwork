package net.avicus.magma.database.table.impl;

import java.util.List;
import java.util.function.BiConsumer;
import net.avicus.magma.database.model.impl.Achievement;
import net.avicus.magma.database.model.impl.AchievementReceiver;
import net.avicus.magma.database.model.impl.User;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;

public class AchievementReceiverTable extends Table<AchievementReceiver> {

  public AchievementReceiverTable(Database database, String name,
      Class<AchievementReceiver> model) {
    super(database, name, model);
  }

  public List<AchievementReceiver> findByUser(int userId) {
    return select().where("user_id", userId).execute();
  }

  public boolean hasAchievement(Achievement achievement, User user) {
    return !select().where("achievement_id", achievement.getId()).where("user_id", user.getId())
        .execute().isEmpty();
  }

  public void removeLower(Achievement achievement, User user, AchievementTable table) {
    achievement.getLower(table).forEach(ac -> {
      if (hasAchievement(ac, user)) {
        delete().where("achievement_id", ac.getId()).where("user_id", user.getId()).execute();
      }
    });
  }

  public void give(Achievement achievement, User user, AchievementTable table,
      BiConsumer<User, Achievement> giveCallback) {
    if (!hasAchievement(achievement, user) && !table.hasHigher(achievement, user, this)) {
      insert(new AchievementReceiver(user.getId(), achievement.getId())).execute();
      removeLower(achievement, user, table);
      giveCallback.accept(user, achievement);
    }
  }
}
