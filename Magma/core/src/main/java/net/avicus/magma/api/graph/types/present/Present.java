package net.avicus.magma.api.graph.types.present;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;

/**
 * Something that can be found in the lobby
 */
public class Present extends AbstractResponse<Present> {

  public Present(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "family": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "human_location": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "human_name": {
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
    return "Present";
  }

  /**
   * Family of the present e.g "Christmas 2017"
   */

  public String getFamily() {
    return (String) get("family");
  }

  public Present setFamily(String arg) {
    optimisticData.put(getKey("family"), arg);
    return this;
  }

  /**
   * Description of the location of the present used in the UI.
   */

  public String getHumanLocation() {
    return (String) get("human_location");
  }

  public Present setHumanLocation(String arg) {
    optimisticData.put(getKey("human_location"), arg);
    return this;
  }

  /**
   * Name of the present used in the UI.
   */

  public String getHumanName() {
    return (String) get("human_name");
  }

  public Present setHumanName(String arg) {
    optimisticData.put(getKey("human_name"), arg);
    return this;
  }

  /**
   * Slug of the present used in plugins to protect against name changes.
   */

  public String getSlug() {
    return (String) get("slug");
  }

  public Present setSlug(String arg) {
    optimisticData.put(getKey("slug"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "family":
        return false;

      case "human_location":
        return false;

      case "human_name":
        return false;

      case "slug":
        return false;

      default:
        return false;
    }
  }
}
