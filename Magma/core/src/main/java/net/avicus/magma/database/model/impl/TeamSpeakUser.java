package net.avicus.magma.database.model.impl;

import lombok.Getter;
import lombok.ToString;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

@ToString
public class TeamSpeakUser extends Model {

  @Getter
  @Id
  @Column
  private int id;

  @Getter
  @Column(name = "user_id")
  private int userId;

  @Getter
  @Column(name = "client_id")
  private int clientId;

  public TeamSpeakUser(int userId, int clientId) {
    this.userId = userId;
    this.clientId = clientId;
  }

  public TeamSpeakUser() {

  }

  public TeamSpeakUser(int id, int userId, int clientId) {
    this.id = id;
    this.userId = userId;
    this.clientId = clientId;
  }
}
