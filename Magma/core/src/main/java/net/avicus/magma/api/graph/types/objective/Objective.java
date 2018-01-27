package net.avicus.magma.api.graph.types.objective;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * The completion of an objective by a user.
 */
public class Objective extends AbstractResponse<Objective> {

  public Objective(JsonObject fields) throws SchemaViolationError {
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

        case "hidden": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "objective_id": {
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
    return "Objective";
  }

  /**
   * Date when this objective was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public Objective setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * If this completion was hidden by a stats reset gadget.
   */

  public Boolean isHidden() {
    return (Boolean) get("hidden");
  }

  public Objective setHidden(Boolean arg) {
    optimisticData.put(getKey("hidden"), arg);
    return this;
  }

  /**
   * ID of the objective which was completed.
   */

  public Integer getObjectiveId() {
    return (Integer) get("objective_id");
  }

  public Objective setObjectiveId(Integer arg) {
    optimisticData.put(getKey("objective_id"), arg);
    return this;
  }

  /**
   * ID of the user who completed the objective.
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public Objective setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "created_at":
        return false;

      case "hidden":
        return false;

      case "objective_id":
        return false;

      case "user_id":
        return false;

      default:
        return false;
    }
  }
}
