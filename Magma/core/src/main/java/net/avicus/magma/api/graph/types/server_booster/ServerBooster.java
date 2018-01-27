package net.avicus.magma.api.graph.types.server_booster;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * An XP booster which applies to a server for a specified amount of time..
 */
public class ServerBooster extends AbstractResponse<ServerBooster> {

  public ServerBooster(JsonObject fields) throws SchemaViolationError {
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

        case "expires_at": {
          DateTime optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = DateTime.parse(jsonAsString(field.getValue(), key));
          }

          responseData.put(key, optional1);

          break;
        }

        case "multiplier": {
          Double optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsDouble(field.getValue(), key);
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

        case "starts_at": {
          DateTime optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = DateTime.parse(jsonAsString(field.getValue(), key));
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
    return "ServerBooster";
  }

  /**
   * Date when this serverbooster was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public ServerBooster setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * When this booster ends.
   */

  public DateTime getExpiresAt() {
    return (DateTime) get("expires_at");
  }

  public ServerBooster setExpiresAt(DateTime arg) {
    optimisticData.put(getKey("expires_at"), arg);
    return this;
  }

  /**
   * Amount XP should be multiplied by while this booster is active.
   */

  public Double getMultiplier() {
    return (Double) get("multiplier");
  }

  public ServerBooster setMultiplier(Double arg) {
    optimisticData.put(getKey("multiplier"), arg);
    return this;
  }

  /**
   * Server that the booster applies to.
   */

  public Integer getServerId() {
    return (Integer) get("server_id");
  }

  public ServerBooster setServerId(Integer arg) {
    optimisticData.put(getKey("server_id"), arg);
    return this;
  }

  /**
   * When this booster begins.
   */

  public DateTime getStartsAt() {
    return (DateTime) get("starts_at");
  }

  public ServerBooster setStartsAt(DateTime arg) {
    optimisticData.put(getKey("starts_at"), arg);
    return this;
  }

  /**
   * Date when this serverbooster was last updated.
   */

  public DateTime getUpdatedAt() {
    return (DateTime) get("updated_at");
  }

  public ServerBooster setUpdatedAt(DateTime arg) {
    optimisticData.put(getKey("updated_at"), arg);
    return this;
  }

  /**
   * ID of the user who owns the booster.
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public ServerBooster setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "created_at":
        return false;

      case "expires_at":
        return false;

      case "multiplier":
        return false;

      case "server_id":
        return false;

      case "starts_at":
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
