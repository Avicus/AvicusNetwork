package net.avicus.magma.database.model.impl;

import java.util.Date;
import lombok.Getter;
import lombok.ToString;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

@ToString
@Getter
public class Vote extends Model {

  @Id
  @Column
  private int id;

  @Column(name = "user_id")
  private int userId;

  @Column
  private String service;

  @Column(name = "cast_at")
  private Date castAt;

  public Vote() {

  }

  public Vote(int userId, String service, Date castAt) {
    this.userId = userId;
    this.service = service;
    this.castAt = castAt;
  }
}
