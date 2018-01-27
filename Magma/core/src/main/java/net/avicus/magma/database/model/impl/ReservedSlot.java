package net.avicus.magma.database.model.impl;

import java.util.Date;
import lombok.Getter;
import lombok.ToString;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

@ToString
public class ReservedSlot extends Model {

  @Getter
  @Id
  @Column
  private int id;

  @Getter
  @Column(name = "team_id")
  private int teamId;

  @Getter
  @Column
  private String server;

  @Getter
  @Column(name = "start_at")
  private Date start;

  @Getter
  @Column(name = "end_at")
  private Date end;

  /**
   * Check if this reservation is ongoing given a certain time.
   */
  public boolean isOngoing(Date date) {
    // if after start time, before end time
    return date.after(this.start) && date.before(this.end);
  }

  /**
   * Check if this reservation is ongoing at the current time.
   */
  public boolean isOngoing() {
    return isOngoing(new Date());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservedSlot that = (ReservedSlot) o;
    return getId() == that.getId();
  }
}
