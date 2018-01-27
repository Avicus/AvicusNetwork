package net.avicus.magma.api.graph.types.death;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * When a player dies in a match, duh.
 */
public class Death extends AbstractResponse<Death> {

  public Death(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "cause": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "cause_hidden": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "created_at": {
          DateTime optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = DateTime.parse(jsonAsString(field.getValue(), key));
          }

          responseData.put(key, optional1);

          break;
        }

        case "user_hidden": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
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
    return "Death";
  }

  /**
   * ID of the user who caused this death.
   */

  public Integer getCause() {
    return (Integer) get("cause");
  }

  public Death setCause(Integer arg) {
    optimisticData.put(getKey("cause"), arg);
    return this;
  }

  /**
   * If the cause has hidden this death (their kill, respectively) with a stats reset.
   */

  public Boolean isCauseHidden() {
    return (Boolean) get("cause_hidden");
  }

  public Death setCauseHidden(Boolean arg) {
    optimisticData.put(getKey("cause_hidden"), arg);
    return this;
  }

  /**
   * Date when this death was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public Death setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * If the user has hidden this death with a stats reset.
   */

  public Boolean isUserHidden() {
    return (Boolean) get("user_hidden");
  }

  public Death setUserHidden(Boolean arg) {
    optimisticData.put(getKey("user_hidden"), arg);
    return this;
  }

  /**
   * ID of the user who died.
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public Death setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "cause":
        return false;

      case "cause_hidden":
        return false;

      case "created_at":
        return false;

      case "user_hidden":
        return false;

      case "user_id":
        return false;

      default:
        return false;
    }
  }
}
