package net.avicus.magma.database.model.impl;

import lombok.Getter;
import lombok.ToString;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

@ToString
@Getter
public class AchievementReceiver extends Model {

  @Id
  @Column
  private int id;

  @Column(name = "user_id")
  private int userId;

  @Column(name = "achievement_id")
  private int achievementId;

  public AchievementReceiver() {

  }

  public AchievementReceiver(int userId, int achievementId) {
    this.userId = userId;
    this.achievementId = achievementId;
  }

  public AchievementReceiver(User user, Achievement achievement) {
    this(user.getId(), achievement.getId());
  }
}
