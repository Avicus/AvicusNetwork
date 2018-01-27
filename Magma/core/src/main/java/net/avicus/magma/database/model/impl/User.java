package net.avicus.magma.database.model.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import net.avicus.magma.NetworkIdentification;
import net.avicus.magma.database.Database;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;
import org.joda.time.Duration;

@ToString
public class User extends Model {

  public static User CONSOLE = new User("Console");
  @Getter
  @Id
  @Column
  private int id;
  @Getter
  @Column(name = "username")
  private String name;
  @Column(unique = true)
  private String uuid;
  @Column
  private String locale;
  @Getter
  @Column(name = "created_at")
  private Date createdAt;
  @Getter
  @Column(name = "verify_key")
  private String verifyKey;
  @Getter
  @Column(name = "verify_key_success")
  private boolean verifyKeySuccessful;
  @Getter
  @Column(name = "discord_id", unique = true)
  private long discordId;

  public User() {

  }

  /**
   * Creates a new user.
   */
  private User(String name, UUID uuid) {
    this.name = name;
    this.uuid = uuid.toString().replace("-", "");
    this.createdAt = new Date();
  }

  private User(String name) {
    this.name = name;
    this.id = 0;
  }

  public static User createUser(Database mysql, String name, UUID uuid) {
    User user = new User(name, uuid);
    mysql.getUsers().insert(user).execute();
    mysql.getUsernames().insert(new Username(user.id, name)).execute();
    return user;
  }

  public boolean isConsole() {
    return this.id == 0;
  }

  public UUID getUniqueId() {
    if (this.uuid == null) {
      throw new RuntimeException("UUID not present in User.");
    }

    StringBuffer sb = new StringBuffer(this.uuid);
    sb.insert(8, "-");

    sb = new StringBuffer(sb.toString());
    sb.insert(13, "-");

    sb = new StringBuffer(sb.toString());
    sb.insert(18, "-");

    sb = new StringBuffer(sb.toString());
    sb.insert(23, "-");

    return UUID.fromString(sb.toString());
  }

  public Locale getLocale() {
    return this.locale == null ? Locale.ENGLISH
        : Locale.forLanguageTag(this.locale.replace("_", "-"));
  }

  public List<RankMember> memberships(Database mysql) {
    return mysql.getRankMembers().findByUser(this);
  }

  public List<Punishment> punishments(Database mysql, Optional<ServerCategory> local) {
    if (local.isPresent()) {
      return mysql.getPunishments().findByUser(this).stream().filter(p -> {
        switch (p.getType()) {
          case BAN:
            return !local.get().getOptions().isIgnoreBans();
          case KICK:
            return !local.get().getOptions().isIgnoreKicks();
          case TEMPBAN:
            return !local.get().getOptions().isIgnoreTempBans();
          case WARN:
            return !local.get().getOptions().isIgnoreWarns();
          default:
            return true;
        }
      }).collect(Collectors.toList());
    } else {
      return mysql.getPunishments().findByUser(this);
    }
  }

  public UserDetail details(Database mysql) {
    return mysql.getUserDetails().findByUser(this);
  }

  public List<Username> usernames(Database mysql) {
    return mysql.getUsernames().findByUser(this);
  }

  public List<User> friends(Database mysql) {
    List<User> ids = new ArrayList<>();
    List<Friend> friends = mysql.getFriends().findByUser(this.id);
    ids.addAll(friends.stream()
        .map(friend -> friend.getFriend(mysql))
        .collect(Collectors.toList()));
    return ids;
  }

  public List<Integer> friendIds(Database mysql, boolean onlyAccepted) {
    List<Integer> ids = new ArrayList<>();
    List<Friend> friends = mysql.getFriends().findByUser(this.id);
    ids.addAll(friends.stream()
        .filter(friend -> !onlyAccepted || friend.isAccepted())
        .map(Friend::getFriendId)
        .collect(Collectors.toList()));
    return ids;
  }

  public Rank getHighestRank(Database mysql) {
    List<RankMember> memberships = mysql.getRankMembers().findByUser(this);

    Rank highest = mysql.getRanks().getOrCreate("@default");

    for (RankMember membership : memberships) {
      Optional<Rank> rank = mysql.getRanks().findById(membership.getRankId());

      if (!rank.isPresent()) {
        continue;
      }

      if (highest.getPriority() < rank.get().getPriority()) {
        highest = rank.get();
      }
    }

    return highest;
  }

  public void updateUsername(Database mysql, String username) {
    mysql.getUsers().update().where("id", this.id).set("username", username).execute();
    mysql.getUsernames().insert(new Username(this.id, username)).execute();
    this.name = username;
  }

  public void resetDiscord(Database mysql) {
    mysql.getUsers().update().where("id", this.id).set("discord_id", null).execute();
    this.discordId = 0;
  }

  public void updateClientPayload(Database mysql, int mcVersion, String locale) {
    mysql.getUsers().update()
        .where("id", this.id)
        .set("mc_version", mcVersion)
        .set("locale", locale)
        .execute();
  }

  public String getProfile(boolean fullUrl) {
    String base = fullUrl ? NetworkIdentification.URL : "/";
    return base + this.name;
  }

  public Duration getTimeOnline(Database database) {
    List<Session> sessions = database.getSessions().select().where("user_id", this.id).execute();
    final AtomicInteger online = new AtomicInteger();

    sessions.forEach(session -> online.addAndGet(session.getDuration()));

    return new Duration(online.get());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    User user = (User) o;

    return user.getId() == this.getId();
  }
}
