package net.avicus.magma.database.model.impl;

import java.util.Date;
import lombok.Getter;
import lombok.ToString;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

@ToString(exclude = "teamId")
public class TeamMember extends Model {

  @Getter
  @Id
  @Column
  private int id;
  @Getter
  @Column(name = "user_id")
  private int userId;
  @Getter
  @Column(name = "team_id")
  private int teamId;
  @Column
  private String role;
  @Getter
  @Column
  private boolean accepted;
  @Getter
  @Column(name = "accepted_at")
  private Date acceptedAt;

  public Role getRole() {
    try {
      return Role.valueOf(this.role.toUpperCase());
    } catch (Exception e) {
      return Role.MEMBER;
    }
  }

  public enum Role {
    MEMBER,
    LEADER
  }
}
