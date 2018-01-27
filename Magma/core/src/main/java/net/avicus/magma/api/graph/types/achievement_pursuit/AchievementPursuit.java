package net.avicus.magma.api.graph.types.achievement_pursuit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;

/**
 * A pursuit towards earning an achievement.
 */
public class AchievementPursuit extends AbstractResponse<AchievementPursuit> {

  public AchievementPursuit(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "slug": {
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
    return "AchievementPursuit";
  }

  /**
   * Slug of the achievement used in plugins to protect against name changes.
   */

  public String getSlug() {
    return (String) get("slug");
  }

  public AchievementPursuit setSlug(String arg) {
    optimisticData.put(getKey("slug"), arg);
    return this;
  }

  /**
   * ID of the user currently in pursuit of the achievement.
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public AchievementPursuit setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "slug":
        return false;

      case "user_id":
        return false;

      default:
        return false;
    }
  }
}
