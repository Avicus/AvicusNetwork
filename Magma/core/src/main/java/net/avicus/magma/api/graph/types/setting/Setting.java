package net.avicus.magma.api.graph.types.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;

/**
 * A setting which a user has configured in game.
 */
public class Setting extends AbstractResponse<Setting> {

  public Setting(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "key": {
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

        case "value": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
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
    return "Setting";
  }

  /**
   * Key of the setting.
   */

  public String getKey() {
    return (String) get("key");
  }

  public Setting setKey(String arg) {
    optimisticData.put(getKey("key"), arg);
    return this;
  }

  /**
   * ID of the user that this setting is for.
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public Setting setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  /**
   * Value of the setting.
   */

  public String getValue() {
    return (String) get("value");
  }

  public Setting setValue(String arg) {
    optimisticData.put(getKey("value"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "key":
        return false;

      case "user_id":
        return false;

      case "value":
        return false;

      default:
        return false;
    }
  }
}
