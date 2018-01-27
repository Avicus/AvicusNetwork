package net.avicus.magma.database.model.impl;

import lombok.Getter;
import lombok.ToString;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

@ToString
@Getter
public class Setting extends Model {

  @Id
  @Column
  private int id;

  @Column(name = "user_id")
  private int userId;

  @Column
  private String key;

  @Column
  private String value;

  public Setting(User user, String key, String value) {
    this.userId = user.getId();
    this.key = key;
    this.value = value;
  }

  public Setting(int userId, String key, String value) {
    this.userId = userId;
    this.key = key;
    this.value = value;
  }

  public Setting() {

  }
}
