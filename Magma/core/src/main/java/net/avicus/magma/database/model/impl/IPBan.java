package net.avicus.magma.database.model.impl;

import java.util.Date;
import lombok.Getter;
import lombok.ToString;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

@ToString
public class IPBan extends Model {

  @Getter
  @Id
  @Column
  private int id;

  @Getter
  @Column(name = "reason")
  private String reason;

  @Getter
  @Column(name = "staff_id")
  private int staffId;

  @Getter
  @Column(name = "ip")
  private String ip;

  @Getter
  @Column(name = "enabled")
  private boolean enabled;

  @Getter
  @Column(name = "excluded_users")
  private String excludedUsers;

  @Getter
  @Column(name = "created_at")
  private Date createdAt;

  public IPBan() {

  }

  public IPBan(int id, String reason, int staffId, String ip, boolean enabled, String excludedUsers,
      Date createdAt) {
    this.id = id;
    this.reason = reason;
    this.staffId = staffId;
    this.ip = ip;
    this.enabled = enabled;
    this.excludedUsers = excludedUsers;
    this.createdAt = createdAt;
  }

  public boolean isUserExcluded(int userId) {
    return getExcludedUsers().contains("- '" + userId + "'");
  }
}
