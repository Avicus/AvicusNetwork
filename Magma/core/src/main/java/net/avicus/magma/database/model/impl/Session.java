package net.avicus.magma.database.model.impl;

import java.util.Date;
import lombok.Getter;
import lombok.ToString;
import net.avicus.magma.database.Database;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;
import net.avicus.quest.model.ModelUpdate;

/**
 * Represents a session on a single MC server.
 * <p>
 * Sessions are created when they join the server. While
 * the server is operational and the player is connected
 * to that server, "expired_at" is set to a value not too
 * far into the future (15-30 seconds, configurable). A
 * session is considered over when the current time is beyond
 * "expired_at". This way, if the server crashes, session rows
 * in the database are not left un-ended, they are considered
 * over 15-30 seconds after the crash.
 */
@ToString
public class Session extends Model {

  @Getter
  @Id
  @Column
  private int id;
  @Getter
  @Column(name = "user_id")
  private int userId;
  @Getter
  @Column
  private String ip;
  @Getter
  @Column(name = "server_id")
  private int serverId;
  @Getter
  @Column(name = "duration")
  private int duration;
  @Getter
  @Column(name = "created_at")
  private Date createdAt;
  @Getter
  @Column(name = "updated_at")
  private Date expiredAt;
  /**
   * True if the session ended gracefully: The player
   * disconnected. Otherwise the server probably crashed.
   */
  @Getter
  @Column
  private boolean graceful;

  public Session() {

  }

  /**
   * Creates a new session.
   */
  public Session(int userId, String ip, int serverId, Date createdAt, Date expiredAt) {
    this.userId = userId;
    this.ip = ip;
    this.serverId = serverId;
    this.createdAt = createdAt;
    this.expiredAt = expiredAt;
  }

  public User getUser(Database mysql) {
    return mysql.getUsers().findById(this.userId).get();
  }

  public Server getServer(Database mysql) {
    return mysql.getServers().findById(this.serverId).get();
  }

  public void updateExpiredAt(Database database, Date expiredAt, boolean graceful) {
    this.graceful = graceful;
    this.duration = (int) Math
        .floor((double) (expiredAt.getTime() - this.createdAt.getTime()) / 1000.0);
    this.expiredAt = expiredAt;

    ModelUpdate update = database.getSessions().update();
    update.set("updated_at", expiredAt).set("graceful", graceful).set("duration", this.duration);
    update.where("id", this.id).execute();
  }

  public boolean isActive() {
    Date now = new Date();
    return now.after(this.createdAt) && now.before(this.expiredAt);
  }
}
