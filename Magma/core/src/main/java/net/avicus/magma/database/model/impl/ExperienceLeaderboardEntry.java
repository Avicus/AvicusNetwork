package net.avicus.magma.database.model.impl;

import lombok.Getter;
import lombok.ToString;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.model.Model;
import org.joda.time.Days;
import org.joda.time.Duration;

@ToString
public class ExperienceLeaderboardEntry extends Model {

  @Getter
  @Column(name = "user_id")
  private int userId;
  @Column
  private int period;
  @Getter
  @Column
  private int level;
  @Getter
  @Column(name = "prestige_level")
  private int prestigeLevel;
  @Getter
  @Column(name = "xp_total")
  private int xpTotal;
  @Getter
  @Column(name = "xp_nebula")
  private int xpNebula;
  @Getter
  @Column(name = "xp_koth")
  private int xpKOTH;
  @Getter
  @Column(name = "xp_ctf")
  private int xpCTF;
  @Getter
  @Column(name = "xp_tdm")
  private int xpTDM;
  @Getter
  @Column(name = "xp_elimination")
  private int xpElimination;
  @Getter
  @Column(name = "xp_sw")
  private int xpSW;
  @Getter
  @Column(name = "xp_walls")
  private int xpWalls;
  @Getter
  @Column(name = "xp_arcade")
  private int xpArcade;

  public ExperienceLeaderboardEntry() {

  }

  public ExperienceLeaderboardEntry(int userId, Period period) {
    this(userId, period, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  }

  public ExperienceLeaderboardEntry(int userId, Period period, int level, int prestigeLevel,
      int xpTotal, int xpNebula, int xpKOTH, int xpCTF, int xpTDM, int xpElimination, int xpSW,
      int xpWalls, int xpArcade) {
    this.userId = userId;
    this.period = period.ordinal();
    this.level = level;
    this.prestigeLevel = prestigeLevel;
    this.xpTotal = xpTotal;
    this.xpNebula = xpNebula;
    this.xpKOTH = xpKOTH;
    this.xpCTF = xpCTF;
    this.xpTDM = xpTDM;
    this.xpElimination = xpElimination;
    this.xpSW = xpSW;
    this.xpWalls = xpWalls;
    this.xpArcade = xpArcade;
  }

  public Period getPeriod() {
    return Period.values()[this.period];
  }

  public void incrementPrestigeLevel() {
    this.prestigeLevel++;
  }

  public void setLevel(double level) {
    this.level = (int) (level * 1000);
  }

  public void incrementTotalXP(int amount) {
    this.xpTotal = this.xpTotal + amount;
  }

  public void incrementNebulaXP(int amount) {
    this.xpNebula = this.xpNebula + amount;
  }

  public void incrementKOTHXP(int amount) {
    this.xpKOTH = this.xpKOTH + amount;
  }

  public void incrementCTFXP(int amount) {
    this.xpCTF = this.xpCTF + amount;
  }

  public void incrementTDMXP(int amount) {
    this.xpTDM = this.xpTDM + amount;
  }

  public void incrementEliminationXP(int amount) {
    this.xpElimination = this.xpElimination + amount;
  }

  public void incrementSWXP(int amount) {
    this.xpSW = this.xpSW + amount;
  }

  public void incrementWallsXP(int amount) {
    this.xpWalls = this.xpWalls + amount;
  }

  public void incrementArcadeXP(int amount) {
    this.xpArcade = this.xpArcade + amount;
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
