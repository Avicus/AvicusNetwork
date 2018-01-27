package net.avicus.magma.api.graph.mutations.friend_remove.remove_response_data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;

/**
 * Information about what the request accompanied (removed, request canceled, not friends, etc).
 */
public class RemoveResponseData extends AbstractResponse<RemoveResponseData> {

  public RemoveResponseData(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "canceled": {
          responseData.put(key, jsonAsBoolean(field.getValue(), key));

          break;
        }

        case "not_friends": {
          responseData.put(key, jsonAsBoolean(field.getValue(), key));

          break;
        }

        case "removed": {
          responseData.put(key, jsonAsBoolean(field.getValue(), key));

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
    return "RemoveResponseData";
  }

  /**
   * If a friend request for this user was canceled.
   */

  public Boolean isCanceled() {
    return (Boolean) get("canceled");
  }

  public RemoveResponseData setCanceled(Boolean arg) {
    optimisticData.put(getKey("canceled"), arg);
    return this;
  }

  /**
   * If the user is not friends with the person they are attempting to remove.
   */

  public Boolean isNotFriends() {
    return (Boolean) get("not_friends");
  }

  public RemoveResponseData setNotFriends(Boolean arg) {
    optimisticData.put(getKey("not_friends"), arg);
    return this;
  }

  /**
   * If a the friend was removed.
   */

  public Boolean isRemoved() {
    return (Boolean) get("removed");
  }

  public RemoveResponseData setRemoved(Boolean arg) {
    optimisticData.put(getKey("removed"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "canceled":
        return false;

      case "not_friends":
        return false;

      case "removed":
        return false;

      default:
        return false;
    }
  }
}
