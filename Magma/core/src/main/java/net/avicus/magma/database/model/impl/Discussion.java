package net.avicus.magma.database.model.impl;

import lombok.Getter;
import lombok.ToString;
import net.avicus.magma.database.Database;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

@ToString
@Getter
public class Discussion extends Model {

  @Id
  @Column
  private int id;

  @Column(name = "user_id")
  private int userId;

  @Column(name = "category_id")
  private int categoryId;

  @Column
  private String uuid;

  public String getTitle(Database database) {
    return database.select("revisions").where("discussion_id", this.getId())
        .order("created_at", "DESC").execute().first()
        .getString("title");
  }

  public User getAuthor(Database database) {
    return database.getUsers().findById(this.getUserId()).get();
  }

  public String getCatName(Database database) {
    return database.select("categories").where("id", this.getCategoryId()).execute().first()
        .getString("name");
  }
}
