package net.avicus.magma.database.model.impl;

import java.util.Date;
import lombok.Getter;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

public class Report extends Model {

  @Getter
  @Id
  @Column
  private int id;

  @Getter
  @Column(name = "creator_id")
  private int creatorId;

  @Getter
  @Column(name = "user_id")
  private int reportedId;

  @Getter
  @Column
  private String reason;

  @Getter
  @Column
  private String server;

  @Getter
  @Column(name = "created_at")
  private Date createdAt;

  public Report() {

  }

  public Report(int creatorId, int reportedId, String reason, String server, Date createdAt) {
    this.creatorId = creatorId;
    this.reportedId = reportedId;
    this.reason = reason;
    this.server = server;
    this.createdAt = createdAt;
  }
}
