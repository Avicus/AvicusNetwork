package net.avicus.magma.api.graph.types.friend;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * Representation of a friendship between 2 users.
 */
public class Friend extends AbstractResponse<Friend> {

  public Friend(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "accepted": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
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

        case "friend_id": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
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
    return "Friend";
  }

  /**
   * If the request has been accepted.
   */

  public Integer getAccepted() {
    return (Integer) get("accepted");
  }

  public Friend setAccepted(Integer arg) {
    optimisticData.put(getKey("accepted"), arg);
    return this;
  }

  /**
   * Date when this friend was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public Friend setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * ID of the user who was requested.
   */

  public Integer getFriendId() {
    return (Integer) get("friend_id");
  }

  public Friend setFriendId(Integer arg) {
    optimisticData.put(getKey("friend_id"), arg);
    return this;
  }

  /**
   * ID of the user who initiated the friendship.
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public Friend setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "accepted":
        return false;

      case "created_at":
        return false;

      case "friend_id":
        return false;

      case "user_id":
        return false;

      default:
        return false;
    }
  }
}
