package net.avicus.magma.api.graph.types.backpack_gadget;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * A gadget housed in a user's backpack.
 */
public class BackpackGadget extends AbstractResponse<BackpackGadget> {

  public BackpackGadget(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "context": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
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

        case "gadget": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "gadget_type": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "old_id": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "updated_at": {
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
    return "BackpackGadget";
  }

  /**
   * Context of the gadget related to the specific user.
   */

  public String getContext() {
    return (String) get("context");
  }

  public BackpackGadget setContext(String arg) {
    optimisticData.put(getKey("context"), arg);
    return this;
  }

  /**
   * Date when this backpackgadget was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public BackpackGadget setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * Special data associated with the gadget regardless of context.
   */

  public String getGadget() {
    return (String) get("gadget");
  }

  public BackpackGadget setGadget(String arg) {
    optimisticData.put(getKey("gadget"), arg);
    return this;
  }

  /**
   * Type of the base gadget of this item.
   */

  public String getGadgetType() {
    return (String) get("gadget_type");
  }

  public BackpackGadget setGadgetType(String arg) {
    optimisticData.put(getKey("gadget_type"), arg);
    return this;
  }

  /**
   * ID of the gadget before the Atlas conversion.
   */

  public Integer getOldId() {
    return (Integer) get("old_id");
  }

  public BackpackGadget setOldId(Integer arg) {
    optimisticData.put(getKey("old_id"), arg);
    return this;
  }

  /**
   * Date when this backpackgadget was last updated.
   */

  public DateTime getUpdatedAt() {
    return (DateTime) get("updated_at");
  }

  public BackpackGadget setUpdatedAt(DateTime arg) {
    optimisticData.put(getKey("updated_at"), arg);
    return this;
  }

  /**
   * ID of the user who has this gadget in their backpack.
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public BackpackGadget setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "context":
        return false;

      case "created_at":
        return false;

      case "gadget":
        return false;

      case "gadget_type":
        return false;

      case "old_id":
        return false;

      case "updated_at":
        return false;

      case "user_id":
        return false;

      default:
        return false;
    }
  }
}
