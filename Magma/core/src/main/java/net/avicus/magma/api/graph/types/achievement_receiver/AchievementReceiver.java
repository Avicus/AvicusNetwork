package net.avicus.magma.api.graph.types.achievement_receiver;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;

/**
 * Link between a user and a specific achievement.
 */
public class AchievementReceiver extends AbstractResponse<AchievementReceiver> {

  public AchievementReceiver(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "achievement_id": {
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
    return "AchievementReceiver";
  }

  /**
   * ID of the achievement that the user is receiving.
   */

  public Integer getAchievementId() {
    return (Integer) get("achievement_id");
  }

  public AchievementReceiver setAchievementId(Integer arg) {
    optimisticData.put(getKey("achievement_id"), arg);
    return this;
  }

  /**
   * ID of the user that received the Achievement
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public AchievementReceiver setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "achievement_id":
        return false;

      case "user_id":
        return false;

      default:
        return false;
    }
  }
}
