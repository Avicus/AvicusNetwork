package net.avicus.magma.database.model.impl;

import java.util.Date;
import lombok.Getter;
import net.avicus.magma.database.Database;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

public class ExperienceTransaction extends Model {

  @Getter
  @Column
  @Id
  private int id;
  @Column(name = "season_id")
  private int seasonId;
  @Getter
  @Column(name = "user_id")
  private int userId;
  @Getter
  @Column
  private int amount;
  @Getter
  @Column
  private String genre;
  @Getter
  @Column(name = "created_at")
  private Date createdAt;

  public ExperienceTransaction(int seasonId, int userId, int amount, String genre, Date createdAt) {
    this.seasonId = seasonId;
    this.userId = userId;
    this.amount = amount;
    this.createdAt = createdAt;
    this.genre = genre;
  }

  public ExperienceTransaction() {

  }

  public PrestigeSeason getSeason(Database database) {
    return database.getSeasons().findById(this.seasonId).get();
  }
}
