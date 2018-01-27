package net.avicus.magma.api.graph.types.punishment;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * A strike against a user caused by them breaking a rule (usually).
 */
public class Punishment extends AbstractResponse<Punishment> {

  public Punishment(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "appealed": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "date": {
          DateTime optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = DateTime.parse(jsonAsString(field.getValue(), key));
          }

          responseData.put(key, optional1);

          break;
        }

        case "expires": {
          DateTime optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = DateTime.parse(jsonAsString(field.getValue(), key));
          }

          responseData.put(key, optional1);

          break;
        }

        case "reason": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "server_id": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "silent": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "staff_id": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "type": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
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
    return "Punishment";
  }

  /**
   * If this punishment has been appealed.
   */

  public Integer getAppealed() {
    return (Integer) get("appealed");
  }

  public Punishment setAppealed(Integer arg) {
    optimisticData.put(getKey("appealed"), arg);
    return this;
  }

  /**
   * Date that this punishment was issued.
   */

  public DateTime getDate() {
    return (DateTime) get("date");
  }

  public Punishment setDate(DateTime arg) {
    optimisticData.put(getKey("date"), arg);
    return this;
  }

  /**
   * Date when this punishment is set to expire.
   */

  public DateTime getExpires() {
    return (DateTime) get("expires");
  }

  public Punishment setExpires(DateTime arg) {
    optimisticData.put(getKey("expires"), arg);
    return this;
  }

  /**
   * The reason this punishment was issued.
   */

  public String getReason() {
    return (String) get("reason");
  }

  public Punishment setReason(String arg) {
    optimisticData.put(getKey("reason"), arg);
    return this;
  }

  /**
   * ID of the server that this punishment was issued on.
   */

  public Integer getServerId() {
    return (Integer) get("server_id");
  }

  public Punishment setServerId(Integer arg) {
    optimisticData.put(getKey("server_id"), arg);
    return this;
  }

  /**
   * If the punishment was displayed in the UI when it was issued.
   */

  public Boolean isSilent() {
    return (Boolean) get("silent");
  }

  public Punishment setSilent(Boolean arg) {
    optimisticData.put(getKey("silent"), arg);
    return this;
  }

  /**
   * User who issued this punishment.
   */

  public Integer getStaffId() {
    return (Integer) get("staff_id");
  }

  public Punishment setStaffId(Integer arg) {
    optimisticData.put(getKey("staff_id"), arg);
    return this;
  }

  /**
   * Type of punishment
   */

  public String getType() {
    return (String) get("type");
  }

  public Punishment setType(String arg) {
    optimisticData.put(getKey("type"), arg);
    return this;
  }

  /**
   * User who received this punishment.
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public Punishment setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "appealed":
        return false;

      case "date":
        return false;

      case "expires":
        return false;

      case "reason":
        return false;

      case "server_id":
        return false;

      case "silent":
        return false;

      case "staff_id":
        return false;

      case "type":
        return false;

      case "user_id":
        return false;

      default:
        return false;
    }
  }
}
