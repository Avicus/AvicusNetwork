package net.avicus.magma.api.graph.types.prestige_level;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;

/**
 * Represents a level of prestige earned by a user in a specific season.
 */
public class PrestigeLevel extends AbstractResponse<PrestigeLevel> {

  public PrestigeLevel(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "level": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "season_id": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "user_id": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
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
    return "PrestigeLevel";
  }

  /**
   * The level that was reached.
   */

  public Integer getLevel() {
    return (Integer) get("level");
  }

  public PrestigeLevel setLevel(Integer arg) {
    optimisticData.put(getKey("level"), arg);
    return this;
  }

  /**
   * ID of the season which this happened in.
   */

  public Integer getSeasonId() {
    return (Integer) get("season_id");
  }

  public PrestigeLevel setSeasonId(Integer arg) {
    optimisticData.put(getKey("season_id"), arg);
    return this;
  }

  /**
   * ID of the user who reached the level.
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public PrestigeLevel setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "level":
        return false;

      case "season_id":
        return false;

      case "user_id":
        return false;

      default:
        return false;
    }
  }
}
