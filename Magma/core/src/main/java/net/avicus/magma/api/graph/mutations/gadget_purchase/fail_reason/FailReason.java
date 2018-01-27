package net.avicus.magma.api.graph.mutations.gadget_purchase.fail_reason;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;

/**
 * The reason the purchase failed.
 */
public class FailReason extends AbstractResponse<FailReason> {

  public FailReason(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "money": {
          responseData.put(key, jsonAsBoolean(field.getValue(), key));

          break;
        }

        case "rank": {
          responseData.put(key, jsonAsBoolean(field.getValue(), key));

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
    return "FailReason";
  }

  /**
   * If the purchase failed due to a lack of currency.
   */

  public Boolean isMoney() {
    return (Boolean) get("money");
  }

  public FailReason setMoney(Boolean arg) {
    optimisticData.put(getKey("money"), arg);
    return this;
  }

  /**
   * If the purchase failed due to a lack of rank.
   */

  public Boolean isRank() {
    return (Boolean) get("rank");
  }

  public FailReason setRank(Boolean arg) {
    optimisticData.put(getKey("rank"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "money":
        return false;

      case "rank":
        return false;

      default:
        return false;
    }
  }
}
