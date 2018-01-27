package net.avicus.magma.api.graph.types.membership;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * Represents a user's participation in a rank.
 */
public class Membership extends AbstractResponse<Membership> {

  public Membership(JsonObject fields) throws SchemaViolationError {
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

        case "expires_at": {
          DateTime optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = DateTime.parse(jsonAsString(field.getValue(), key));
          }

          responseData.put(key, optional1);

          break;
        }

        case "is_purchased": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "member_id": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "rank_id": {
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
    return "Membership";
  }

  /**
   * Date when this membership was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public Membership setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * When this membership expires.
   */

  public DateTime getExpiresAt() {
    return (DateTime) get("expires_at");
  }

  public Membership setExpiresAt(DateTime arg) {
    optimisticData.put(getKey("expires_at"), arg);
    return this;
  }

  /**
   * If this rank was purchased from the store.
   */

  public Boolean isPurchased() {
    return (Boolean) get("is_purchased");
  }

  public Membership setIsPurchased(Boolean arg) {
    optimisticData.put(getKey("is_purchased"), arg);
    return this;
  }

  /**
   * ID of the user who this membership is for.
   */

  public Integer getMemberId() {
    return (Integer) get("member_id");
  }

  public Membership setMemberId(Integer arg) {
    optimisticData.put(getKey("member_id"), arg);
    return this;
  }

  /**
   * ID of the rank which the user belongs to.
   */

  public Integer getRankId() {
    return (Integer) get("rank_id");
  }

  public Membership setRankId(Integer arg) {
    optimisticData.put(getKey("rank_id"), arg);
    return this;
  }

  /**
   * Date when this membership was last updated.
   */

  public DateTime getUpdatedAt() {
    return (DateTime) get("updated_at");
  }

  public Membership setUpdatedAt(DateTime arg) {
    optimisticData.put(getKey("updated_at"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "created_at":
        return false;

      case "expires_at":
        return false;

      case "is_purchased":
        return false;

      case "member_id":
        return false;

      case "rank_id":
        return false;

      case "updated_at":
        return false;

      default:
        return false;
    }
  }
}
