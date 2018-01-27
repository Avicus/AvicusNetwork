package net.avicus.magma.api.graph.mutations.gadget_update;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;

/**
 * Autogenerated return type of GadgetUpdate
 */
public class GadgetUpdate extends AbstractResponse<GadgetUpdate> {

  public GadgetUpdate(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "clientMutationId": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "remove": {
          responseData.put(key, jsonAsBoolean(field.getValue(), key));

          break;
        }

        case "usages_remaining": {
          responseData.put(key, jsonAsInteger(field.getValue(), key));

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
    return "GadgetUpdatePayload";
  }

  /**
   * A unique identifier for the client performing the mutation.
   */

  public String getClientMutationId() {
    return (String) get("clientMutationId");
  }

  public GadgetUpdate setClientMutationId(String arg) {
    optimisticData.put(getKey("clientMutationId"), arg);
    return this;
  }

  /**
   * If the gadget should be removed from the user's backpack.
   */

  public Boolean isRemove() {
    return (Boolean) get("remove");
  }

  public GadgetUpdate setRemove(Boolean arg) {
    optimisticData.put(getKey("remove"), arg);
    return this;
  }

  /**
   * Amount of usages remaining AFTER the current use.
   */

  public Integer getUsagesRemaining() {
    return (Integer) get("usages_remaining");
  }

  public GadgetUpdate setUsagesRemaining(Integer arg) {
    optimisticData.put(getKey("usages_remaining"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "clientMutationId":
        return false;

      case "remove":
        return false;

      case "usages_remaining":
        return false;

      default:
        return false;
    }
  }
}