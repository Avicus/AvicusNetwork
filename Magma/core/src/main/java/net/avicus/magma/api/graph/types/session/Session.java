package net.avicus.magma.api.graph.types.session;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * A span of time a user spent on a server.
 */
public class Session extends AbstractResponse<Session> {

  public Session(JsonObject fields) throws SchemaViolationError {
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

        case "duration": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "graceful": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "ip": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "is_active": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
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
    return "Session";
  }

  /**
   * Date when this session was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public Session setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * How long (in seconds) that the session lasted.
   */

  public Integer getDuration() {
    return (Integer) get("duration");
  }

  public Session setDuration(Integer arg) {
    optimisticData.put(getKey("duration"), arg);
    return this;
  }

  /**
   * If the session ended without a crash.
   */

  public Boolean isGraceful() {
    return (Boolean) get("graceful");
  }

  public Session setGraceful(Boolean arg) {
    optimisticData.put(getKey("graceful"), arg);
    return this;
  }

  /**
   * IP of the user during the session
   */

  public String getIp() {
    return (String) get("ip");
  }

  public Session setIp(String arg) {
    optimisticData.put(getKey("ip"), arg);
    return this;
  }

  /**
   * If the session is still ongoing.
   */

  public Boolean isActive() {
    return (Boolean) get("is_active");
  }

  public Session setIsActive(Boolean arg) {
    optimisticData.put(getKey("is_active"), arg);
    return this;
  }

  /**
   * ID of the server that the session happened on.
   */

  public Integer getServerId() {
    return (Integer) get("server_id");
  }

  public Session setServerId(Integer arg) {
    optimisticData.put(getKey("server_id"), arg);
    return this;
  }

  /**
   * Date when this session was last updated.
   */

  public DateTime getUpdatedAt() {
    return (DateTime) get("updated_at");
  }

  public Session setUpdatedAt(DateTime arg) {
    optimisticData.put(getKey("updated_at"), arg);
    return this;
  }

  /**
   * ID of the user that was on the server.
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public Session setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "created_at":
        return false;

      case "duration":
        return false;

      case "graceful":
        return false;

      case "ip":
        return false;

      case "is_active":
        return false;

      case "server_id":
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
