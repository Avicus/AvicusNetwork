package net.avicus.magma.api.graph.types.map_rating;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * A 1-5 rating by a user for a specific version of a map.
 */
public class MapRating extends AbstractResponse<MapRating> {

  public MapRating(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "created_at": {
          DateTime optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = DateTime.parse(jsonAsString(field.getValue(), key));
          }

          responseData.put(key, optional1);

          break;
        }

        case "feedback": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "map_slug": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "map_version": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "player": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "rating": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "updated_at": {
          DateTime optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = DateTime.parse(jsonAsString(field.getValue(), key));
          }

          responseData.put(key, optional1);

          break;
        }

        case "__typename": {
          responseData.put(key, jsonAsString(field.getValue(), key));
          break;
        }
        default: {
          throw new SchemaViolationError(this, key, field.getValue());
        }
      }
    }
  }

  public String getGraphQlTypeName() {
    return "MapRating";
  }

  /**
   * Date when this maprating was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public MapRating setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * Feedback entered in the feedback book.
   */

  public String getFeedback() {
    return (String) get("feedback");
  }

  public MapRating setFeedback(String arg) {
    optimisticData.put(getKey("feedback"), arg);
    return this;
  }

  /**
   * Slug of the map which this rating is for.
   */

  public String getMapSlug() {
    return (String) get("map_slug");
  }

  public MapRating setMapSlug(String arg) {
    optimisticData.put(getKey("map_slug"), arg);
    return this;
  }

  /**
   * Version of the map which this rating is for.
   */

  public String getMapVersion() {
    return (String) get("map_version");
  }

  public MapRating setMapVersion(String arg) {
    optimisticData.put(getKey("map_version"), arg);
    return this;
  }

  /**
   * ID of the user who rated this map.
   */

  public Integer getPlayer() {
    return (Integer) get("player");
  }

  public MapRating setPlayer(Integer arg) {
    optimisticData.put(getKey("player"), arg);
    return this;
  }

  /**
   * Rating which the user gave for this map version.
   */

  public Integer getRating() {
    return (Integer) get("rating");
  }

  public MapRating setRating(Integer arg) {
    optimisticData.put(getKey("rating"), arg);
    return this;
  }

  /**
   * Date when this maprating was last updated.
   */

  public DateTime getUpdatedAt() {
    return (DateTime) get("updated_at");
  }

  public MapRating setUpdatedAt(DateTime arg) {
    optimisticData.put(getKey("updated_at"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "created_at":
        return false;

      case "feedback":
        return false;

      case "map_slug":
        return false;

      case "map_version":
        return false;

      case "player":
        return false;

      case "rating":
        return false;

      case "updated_at":
        return false;

      default:
        return false;
    }
  }
}
