package net.avicus.magma.api.graph.types.present_finder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;

/**
 * Link between a user and a specific present.
 */
public class PresentFinder extends AbstractResponse<PresentFinder> {

  public PresentFinder(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "present_id": {
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
    return "PresentFinder";
  }

  /**
   * ID of the present that the user found.
   */

  public Integer getPresentId() {
    return (Integer) get("present_id");
  }

  public PresentFinder setPresentId(Integer arg) {
    optimisticData.put(getKey("present_id"), arg);
    return this;
  }

  /**
   * ID of the user that found the present.
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public PresentFinder setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "present_id":
        return false;

      case "user_id":
        return false;

      default:
        return false;
    }
  }
}
