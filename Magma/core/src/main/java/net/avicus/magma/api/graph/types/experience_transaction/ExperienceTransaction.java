package net.avicus.magma.api.graph.types.experience_transaction;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * An XP reward a user receives for completing various tasks in game.
 */
public class ExperienceTransaction extends AbstractResponse<ExperienceTransaction> {

  public ExperienceTransaction(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "amount": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
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

        case "genre": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "season_id": {
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

        case "weight": {
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
    return "ExperienceTransaction";
  }

  /**
   * Number of XP this transaction should represent.
   */

  public Integer getAmount() {
    return (Integer) get("amount");
  }

  public ExperienceTransaction setAmount(Integer arg) {
    optimisticData.put(getKey("amount"), arg);
    return this;
  }

  /**
   * Date when this experiencetransaction was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public ExperienceTransaction setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * Game genre of this transaction.
   */

  public String getGenre() {
    return (String) get("genre");
  }

  public ExperienceTransaction setGenre(String arg) {
    optimisticData.put(getKey("genre"), arg);
    return this;
  }

  /**
   * ID of the season which this transaction happened inside of
   */

  public Integer getSeasonId() {
    return (Integer) get("season_id");
  }

  public ExperienceTransaction setSeasonId(Integer arg) {
    optimisticData.put(getKey("season_id"), arg);
    return this;
  }

  /**
   * ID of the user that the XP in this transaction is rewarded to.
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public ExperienceTransaction setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  /**
   * The base XP value was multiplied by. The amount represented by this object already reflects
   * this operation.
   */

  public String getWeight() {
    return (String) get("weight");
  }

  public ExperienceTransaction setWeight(String arg) {
    optimisticData.put(getKey("weight"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "amount":
        return false;

      case "created_at":
        return false;

      case "genre":
        return false;

      case "season_id":
        return false;

      case "user_id":
        return false;

      case "weight":
        return false;

      default:
        return false;
    }
  }
}
