package net.avicus.magma.api.graph.types.alert;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * Link between a user and a specific achievement.
 */
public class Alert extends AbstractResponse<Alert> {

  public Alert(JsonObject fields) throws SchemaViolationError {
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

        case "id": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "message": {
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

        case "seen": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "url": {
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
    return "Alert";
  }

  /**
   * When the alert was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public Alert setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * The ID of the alert
   */

  public Integer getId() {
    return (Integer) get("id");
  }

  /**
   * The message which is displayed to the user.
   */

  public String getMessage() {
    return (String) get("message");
  }

  public Alert setMessage(String arg) {
    optimisticData.put(getKey("message"), arg);
    return this;
  }

  /**
   * Unique name of the Alert.
   */

  public String getName() {
    return (String) get("name");
  }

  public Alert setName(String arg) {
    optimisticData.put(getKey("name"), arg);
    return this;
  }

  /**
   * If the alert has been read.
   */

  public Boolean isSeen() {
    return (Boolean) get("seen");
  }

  public Alert setSeen(Boolean arg) {
    optimisticData.put(getKey("seen"), arg);
    return this;
  }

  /**
   * URL that this alert will direct to when clicked.
   */

  public String getUrl() {
    return (String) get("url");
  }

  public Alert setUrl(String arg) {
    optimisticData.put(getKey("url"), arg);
    return this;
  }

  /**
   * ID of the user that this alert is for.
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public Alert setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "created_at":
        return false;

      case "id":
        return false;

      case "message":
        return false;

      case "name":
        return false;

      case "seen":
        return false;

      case "url":
        return false;

      case "user_id":
        return false;

      default:
        return false;
    }
  }
}
