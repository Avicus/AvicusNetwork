package net.avicus.magma.database.model.impl;

import lombok.Getter;
import lombok.ToString;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

@ToString
@Getter
public class AchievementPursuit extends Model {

  @Id
  @Column
  private int id;

  @Column
  private String slug;

  @Column(name = "user_id")
  private int userId;

  @Column
  private int progress;

  public AchievementPursuit() {

  }

  public AchievementPursuit(String slug, int userId, int value) {
    this.slug = slug;
    this.userId = userId;
    this.progress = value;
  }

  public AchievementPursuit(String slug, User user, int value) {
    this(slug, user.getId(), value);
  }
}
