package net.avicus.magma.database.model.impl;

import java.util.Date;
import lombok.Getter;
import net.avicus.magma.database.Database;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

public class Friend extends Model {

  @Getter
  @Id
  @Column
  private int id;

  @Getter
  @Column(name = "user_id")
  private int userId;
  @Getter
  @Column(name = "friend_id")
  private int friendId;
  @Getter
  @Column
  private boolean accepted;
  @Getter
  @Column(name = "created_at")
  private Date createdAt;

  public Friend() {

  }

  /**
   * Creates a new friendship <3.
   */
  public Friend(int userId, int friendId, boolean accepted) {
    this.userId = userId;
    this.friendId = friendId;
    this.accepted = accepted;
    this.createdAt = new Date();
  }

  public User getUser(Database mysql) {
    return mysql.getUsers().findById(this.userId).get();
  }

  public User getFriend(Database mysql) {
    return mysql.getUsers().findById(this.friendId).get();
  }

  public void setAccepted(Database mysql) {
    mysql.getFriends().update().set("accepted", true).where("id", this.id).execute();
  }
}
