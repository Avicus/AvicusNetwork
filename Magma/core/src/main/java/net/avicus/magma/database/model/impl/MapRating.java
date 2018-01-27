package net.avicus.magma.database.model.impl;

import java.util.Date;
import lombok.Setter;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

public class MapRating extends Model {

  @Column
  @Id
  private int id;
  @Column(name = "map_slug")
  private String mapSlug;
  @Column(name = "map_version")
  private String mapVersion;
  @Column
  private int player;
  @Column
  private int rating;
  @Column
  @Setter
  private String feedback;
  @Column(name = "created_at")
  private Date createdAt;
  @Column(name = "updated_at")
  private Date updatedAt;

  public MapRating() {
  }

  public MapRating(String mapSlug, String mapVersion, int player, int rating) {
    this.mapSlug = mapSlug;
    this.mapVersion = mapVersion;
    this.player = player;
    this.rating = rating;
    this.createdAt = new Date();
    this.updatedAt = this.createdAt;
  }
}
