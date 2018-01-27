package net.avicus.magma.api.graph.types.appeal;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * An attempt by a user to get a punishment removed and/or appealed.
 */
public class Appeal extends AbstractResponse<Appeal> {

  public Appeal(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "appealed": {
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

        case "escalated": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "locked": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "open": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "punishment_id": {
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
    return "Appeal";
  }

  /**
   * If the punishment attached to this appeal has been appealed.
   */

  public Boolean isAppealed() {
    return (Boolean) get("appealed");
  }

  public Appeal setAppealed(Boolean arg) {
    optimisticData.put(getKey("appealed"), arg);
    return this;
  }

  /**
   * Date when this appeal was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public Appeal setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * If the appeal has been escalated, allowing only higher staff to comment.
   */

  public Boolean isEscalated() {
    return (Boolean) get("escalated");
  }

  public Appeal setEscalated(Boolean arg) {
    optimisticData.put(getKey("escalated"), arg);
    return this;
  }

  /**
   * If the appeal is locked from comments.
   */

  public Boolean isLocked() {
    return (Boolean) get("locked");
  }

  public Appeal setLocked(Boolean arg) {
    optimisticData.put(getKey("locked"), arg);
    return this;
  }

  /**
   * If the appeal is open for comments.
   */

  public Boolean isOpen() {
    return (Boolean) get("open");
  }

  public Appeal setOpen(Boolean arg) {
    optimisticData.put(getKey("open"), arg);
    return this;
  }

  /**
   * ID of the punishment that this appeal is for.
   */

  public Integer getPunishmentId() {
    return (Integer) get("punishment_id");
  }

  public Appeal setPunishmentId(Integer arg) {
    optimisticData.put(getKey("punishment_id"), arg);
    return this;
  }

  /**
   * Date when this appeal was last updated.
   */

  public DateTime getUpdatedAt() {
    return (DateTime) get("updated_at");
  }

  public Appeal setUpdatedAt(DateTime arg) {
    optimisticData.put(getKey("updated_at"), arg);
    return this;
  }

  /**
   * ID of the user who started the appeal.
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public Appeal setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "appealed":
        return false;

      case "created_at":
        return false;

      case "escalated":
        return false;

      case "locked":
        return false;

      case "open":
        return false;

      case "punishment_id":
        return false;

      case "updated_at":
        return false;

      case "user_id":
        return false;

      default:
        return false;
    }
  }
}
