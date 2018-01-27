package net.avicus.magma.api.graph.types.achievement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;

/**
 * Something that can be earned
 */
public class Achievement extends AbstractResponse<Achievement> {

  public Achievement(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "description": {
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

        case "slug": {
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
    return "Achievement";
  }

  /**
   * Description of the achievement used in the UI.
   */

  public String getDescription() {
    return (String) get("description");
  }

  public Achievement setDescription(String arg) {
    optimisticData.put(getKey("description"), arg);
    return this;
  }

  /**
   * Name of the achievement used in the UI.
   */

  public String getName() {
    return (String) get("name");
  }

  public Achievement setName(String arg) {
    optimisticData.put(getKey("name"), arg);
    return this;
  }

  /**
   * Slug of the achievement used in plugins to protect against name changes.
   */

  public String getSlug() {
    return (String) get("slug");
  }

  public Achievement setSlug(String arg) {
    optimisticData.put(getKey("slug"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "description":
        return false;

      case "name":
        return false;

      case "slug":
        return false;

      default:
        return false;
    }
  }
}
