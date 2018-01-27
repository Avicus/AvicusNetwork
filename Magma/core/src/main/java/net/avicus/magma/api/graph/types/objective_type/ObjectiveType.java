package net.avicus.magma.api.graph.types.objective_type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;

/**
 * A type of objective which can be completed.
 */
public class ObjectiveType extends AbstractResponse<ObjectiveType> {

  public ObjectiveType(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "name": {
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
    return "ObjectiveType";
  }

  /**
   * Name of the objective type.
   */

  public String getName() {
    return (String) get("name");
  }

  public ObjectiveType setName(String arg) {
    optimisticData.put(getKey("name"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "name":
        return false;

      default:
        return false;
    }
  }
}
