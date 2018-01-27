package net.avicus.magma.database.model.impl;

import java.util.Date;
import lombok.Getter;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

public class ObjectiveCompletion extends Model {

  @Getter
  @Id
  @Column
  private int id;
  @Getter
  @Column(name = "user_id")
  private int userId;
  @Getter
  @Column(name = "objective_id")
  private int objectiveTypeId;
  @Getter
  @Column(name = "created_at")
  private Date date;

  public ObjectiveCompletion(int userId, int objectiveTypeId, Date date) {
    this.userId = userId;
    this.objectiveTypeId = objectiveTypeId;
    this.date = date;
  }

  public ObjectiveCompletion(int userId, ObjectiveType objectiveType, Date date) {
    this(userId, objectiveType.getId(), date);
  }

  public ObjectiveCompletion() {

  }
}
