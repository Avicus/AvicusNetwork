package net.avicus.magma.database.model.impl;

import java.util.Date;
import lombok.Getter;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

public class RankMember extends Model {

  @Getter
  @Id
  @Column
  private int id;
  @Getter
  @Column(name = "member_id")
  private int userId;
  @Getter
  @Column(name = "rank_id")
  private int rankId;
  @Getter
  @Column(name = "expires_at")
  private Date expiresAt;

  public RankMember(int userId, int rankId, Date expiresAt) {
    this.userId = userId;
    this.rankId = rankId;
    this.expiresAt = expiresAt;
  }

  public RankMember() {

  }
}
