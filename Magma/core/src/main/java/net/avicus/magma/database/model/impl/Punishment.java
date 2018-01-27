package net.avicus.magma.database.model.impl;

import java.util.Date;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import net.avicus.magma.database.Database;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

@ToString
public class Punishment extends Model {

  @Getter
  @Id
  @Column
  private int id;

  @Getter
  @Column(name = "user_id")
  private int userId;
  @Getter
  @Column(name = "staff_id")
  private int staffId;
  @Column
  private String type;
  @Getter
  @Column
  private String reason;
  @Getter
  @Column
  private Date date;
  @Column(name = "expires")
  private Date expiry;
  @Getter
  @Column
  private boolean appealed;
  @Getter
  @Column(name = "server_id")
  private int serverId;
  @Getter
  @Column(name = "silent")
  private boolean silent;

  public Punishment() {

  }

  /**
   * Creates a new punishment.
   */
  public Punishment(int user, int staff, Type type, String reason, Date date, Optional<Date> expiry,
      boolean appealed, boolean silent, int serverId) {
    this.userId = user;
    this.staffId = staff;
    this.type = type.name().toLowerCase();
    this.reason = reason;
    this.date = date;
    this.expiry = expiry.orElse(null);
    this.appealed = appealed;
    this.silent = silent;
    this.serverId = serverId;
  }

  public User getUser(Database mysql) {
    return mysql.getUsers().findById(this.userId).get();
  }

  public User getStaff(Database mysql) {
    return this.staffId == 0 ? User.CONSOLE : mysql.getUsers().findById(this.staffId).get();
  }

  public Type getType() {
    return this.type == null ? null : Type.valueOf(this.type.toUpperCase());
  }

  public Optional<Date> getExpiry() {
    return Optional.ofNullable(this.expiry);
  }

  public boolean prohibitLogin() {
    if (this.appealed) {
      return false;
    }

    if (getType() == Type.BAN) {
      return true;
    }
    if (getType() == Type.TEMPBAN && getExpiry().isPresent()) {
      Date expiry = getExpiry().get();
      Date now = new Date();
      return now.toInstant().isBefore(expiry.toInstant());
    }

    return false;
  }

  public boolean mutePlayer() {
    if (this.appealed || getType() != Type.MUTE) {
      return false;
    }

    if (!getExpiry().isPresent()) {
      return true;
    } else {
      Date expiry = getExpiry().get();
      Date now = new Date();
      return now.toInstant().isBefore(expiry.toInstant());
    }
  }

  public boolean kickOnPunish() {
    return getType() != Type.WARN && getType() != Type.MUTE;
  }

  /**
   * The type of punishment.
   */
  public enum Type {
    MUTE,
    WARN,
    KICK,
    BAN,
    TEMPBAN,
    WEB_BAN,
    WEB_TEMPBAN,
    TOURNAMENT_BAN,
    DISCORD_WARN,
    DISCORD_KICK,
    DISCORD_TEMPBAN,
    DISCORD_BAN
  }
}
