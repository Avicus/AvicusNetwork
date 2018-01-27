package net.avicus.magma.api.graph.types.username;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * Representation of non-unique usernames which differnet users have had at some point in time.
 */
public class Username extends AbstractResponse<Username> {

  public Username(JsonObject fields) throws SchemaViolationError {
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

        case "user_id": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "username": {
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
    return "Username";
  }

  /**
   * Date when this username was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public Username setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * ID of the user who had this username.
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public Username setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  /**
   * The username that the userhad.
   */

  public String getUsername() {
    return (String) get("username");
  }

  public Username setUsername(String arg) {
    optimisticData.put(getKey("username"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "created_at":
        return false;

      case "user_id":
        return false;

      case "username":
        return false;

      default:
        return false;
    }
  }
}
