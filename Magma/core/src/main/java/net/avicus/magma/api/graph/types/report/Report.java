package net.avicus.magma.api.graph.types.report;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * A report made by a user accusing another user of violating rule(s).
 */
public class Report extends AbstractResponse<Report> {

  public Report(JsonObject fields) throws SchemaViolationError {
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

        case "creator_id": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "reason": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "server": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
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
    return "Report";
  }

  /**
   * Date when this report was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public Report setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * ID of the user who made the report.
   */

  public Integer getCreatorId() {
    return (Integer) get("creator_id");
  }

  public Report setCreatorId(Integer arg) {
    optimisticData.put(getKey("creator_id"), arg);
    return this;
  }

  /**
   * The reason this report was made.
   */

  public String getReason() {
    return (String) get("reason");
  }

  public Report setReason(String arg) {
    optimisticData.put(getKey("reason"), arg);
    return this;
  }

  /**
   * Name of the server which this report was made on.
   */

  public String getServer() {
    return (String) get("server");
  }

  public Report setServer(String arg) {
    optimisticData.put(getKey("server"), arg);
    return this;
  }

  /**
   * ID of the user who is being reported.
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public Report setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "created_at":
        return false;

      case "creator_id":
        return false;

      case "reason":
        return false;

      case "server":
        return false;

      case "user_id":
        return false;

      default:
        return false;
    }
  }
}
