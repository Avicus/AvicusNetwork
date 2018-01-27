package net.avicus.magma.database.model.impl;

import lombok.Getter;
import net.avicus.magma.database.Database;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

public class PrestigeLevel extends Model {

  @Getter
  @Id
  @Column
  private int id;

  @Getter
  @Column(name = "user_id")
  private int userId;

  @Column(name = "season_id")
  private int seasonId;
  @Getter
  @Column
  private int level;

  public PrestigeLevel(int userId, int seasonId, int level) {
    this.userId = userId;
    this.seasonId = seasonId;
    this.level = level;
  }

  public PrestigeLevel() {
  }

  public PrestigeSeason getSeason(Database database) {
    return database.getSeasons().findById(this.seasonId).get();
  }

  public User getUser(Database database) {
    return database.getUsers().findById(this.getUserId()).get();
  }
}
