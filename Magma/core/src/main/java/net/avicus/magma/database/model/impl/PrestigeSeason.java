package net.avicus.magma.database.model.impl;

import java.util.Date;
import lombok.Getter;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

public class PrestigeSeason extends Model {

  @Getter
  @Id
  @Column
  private int id;

  @Getter
  private String name;

  @Getter
  @Column
  private double multiplier;

  @Getter
  @Column(name = "start_at")
  private Date start;

  @Getter
  @Column(name = "end_at")
  private Date end;

  /**
   * Check if this season is ongoing given a certain time.
   */
  public boolean isOngoing(Date date) {
    // if after start time, before end time
    return date.after(this.start) && date.before(this.end);
  }

  /**
   * Check if this season is ongoing at the current time.
   */
  public boolean isOngoing() {
    return isOngoing(new Date());
  }
}
