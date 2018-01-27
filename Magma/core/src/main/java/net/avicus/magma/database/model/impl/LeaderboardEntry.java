package net.avicus.magma.database.model.impl;

import lombok.Getter;
import lombok.ToString;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.model.Model;
import org.joda.time.Days;
import org.joda.time.Duration;

@ToString
public class LeaderboardEntry extends Model {

  @Getter
  @Column(name = "user_id")
  private int userId;
  @Column
  private int period;
  @Getter
  @Column(name = "time_online")
  private int timeOnline;
  @Getter
  @Column
  private int kills;
  @Getter
  @Column
  private int deaths;
  @Getter
  @Column
  private float kd_ratio;
  @Getter
  @Column
  private int monuments;
  @Getter
  @Column
  private int wools;
  @Getter
  @Column
  private int flags;
  @Getter
  @Column
  private int hills;
  @Getter
  @Column
  private int score;

  public LeaderboardEntry() {

  }

  public LeaderboardEntry(int userId, Period period) {
    this(userId, period, 0, 0, 0, 0, 0, 0, 0);
  }

  public LeaderboardEntry(int userId, Period period, int kills, int deaths, int monuments,
      int wools, int flags, int hills, int score) {
    this.userId = userId;
    this.period = period.ordinal();
    this.kills = kills;
    this.deaths = deaths;
    updateKdRatio();
    this.monuments = monuments;
    this.wools = wools;
    this.flags = flags;
    this.hills = hills;
    this.score = score;
  }

  public Period getPeriod() {
    return Period.values()[this.period];
  }

  public void incrementTimeOnline(int seconds) {
    this.timeOnline += seconds;
  }

  public void incrementKills() {
    this.kills++;
  }

  public void incrementDeaths() {
    this.deaths++;
  }

  public void updateKdRatio() {
    float kills = this.kills;
    float deaths = this.deaths == 0 ? 1 : this.deaths;
    this.kd_ratio = kills / deaths;
  }

  public void incrementMonuments() {
    this.monuments++;
  }

  public void incrementWools() {
    this.wools++;
  }

  public void incrementFlags() {
    this.flags++;
  }

  public void incrementHills() {
    this.hills++;
  }

  public void incrementScore() {
    this.score++;
  }

  public enum Period {
    WEEKLY(Days.days(7).toStandardDuration()),
    MONTHLY(Days.days(31).toStandardDuration()),
    OVERALL(null);

    @Getter
    private final Duration duration;

    Period(Duration duration) {
      this.duration = duration;
    }
  }
}
