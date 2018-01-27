package net.avicus.magma.database.table.impl;

import java.util.Date;
import java.util.Optional;
import net.avicus.magma.database.model.impl.MapRating;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.ModelList;
import net.avicus.quest.model.Table;
import net.avicus.quest.query.Filter;

public class MapRatingTable extends Table<MapRating> {

  public MapRatingTable(Database database, String name, Class<MapRating> model) {
    super(database, name, model);
  }

  public Optional<MapRating> findRating(String mapSlug, String mapVersion, int playerId) {
    final ModelList<MapRating> list = this.select()
        .where(this.basicFilter(mapSlug, mapVersion, playerId))
        .limit(1)
        .execute();
    return list.isEmpty() ? Optional.empty() : Optional.of(list.first());
  }

  public Optional<MapRating> findRating(String mapSlug, String mapVersion, int playerId,
      int rating) {
    final ModelList<MapRating> list = this.select()
        .where(this.basicFilter(mapSlug, mapVersion, playerId))
        .where("rating", rating)
        .limit(1)
        .execute();
    return list.isEmpty() ? Optional.empty() : Optional.of(list.first());
  }

  public void setRating(String mapSlug, String mapVersion, int playerId, int rating) {
    final ModelList<MapRating> list = this.select()
        .where(this.basicFilter(mapSlug, mapVersion, playerId))
        .limit(1)
        .execute();
    if (list.isEmpty()) {
      this.insert(new MapRating(mapSlug, mapVersion, playerId, rating)).execute();
    } else {
      this.update()
          .where(this.basicFilter(mapSlug, mapVersion, playerId))
          .set("rating", rating)
          .set("feedback", null)
          .set("updated_at", new Date())
          .execute();
    }
  }

  public void setFeedback(String mapSlug, String mapVersion, int playerId, String feedback) {
    this.update()
        .where(this.basicFilter(mapSlug, mapVersion, playerId))
        .set("feedback", feedback)
        .set("updated_at", new Date())
        .execute();
  }

  private Filter basicFilter(String mapSlug, String mapVersion, int playerId) {
    return new Filter()
        .where("map_slug", mapSlug)
        .where("map_version", mapVersion)
        .where("player", playerId);
  }
}
