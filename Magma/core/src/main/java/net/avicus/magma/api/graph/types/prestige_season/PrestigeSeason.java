package net.avicus.magma.api.graph.types.prestige_season;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * A period of time in which XP transactions and leveling are grouped together.
 */
public class PrestigeSeason extends AbstractResponse<PrestigeSeason> {

  public PrestigeSeason(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "end_at": {
          DateTime optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = DateTime.parse(jsonAsString(field.getValue(), key));
          }

          responseData.put(key, optional1);

          break;
        }

        case "multiplier": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "name": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "start_at": {
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
    return "PrestigeSeason";
  }

  /**
   * When the season ends.
   */

  public DateTime getEndAt() {
    return (DateTime) get("end_at");
  }

  public PrestigeSeason setEndAt(DateTime arg) {
    optimisticData.put(getKey("end_at"), arg);
    return this;
  }

  /**
   * Value which all XP transactions inside of this season should be multiplied by.
   */

  public String getMultiplier() {
    return (String) get("multiplier");
  }

  public PrestigeSeason setMultiplier(String arg) {
    optimisticData.put(getKey("multiplier"), arg);
    return this;
  }

  /**
   * Name of the season.
   */

  public String getName() {
    return (String) get("name");
  }

  public PrestigeSeason setName(String arg) {
    optimisticData.put(getKey("name"), arg);
    return this;
  }

  /**
   * When the season starts.
   */

  public DateTime getStartAt() {
    return (DateTime) get("start_at");
  }

  public PrestigeSeason setStartAt(DateTime arg) {
    optimisticData.put(getKey("start_at"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "end_at":
        return false;

      case "multiplier":
        return false;

      case "name":
        return false;

      case "start_at":
        return false;

      default:
        return false;
    }
  }
}
