package net.avicus.magma.api.graph.types.reserved_slot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * A server reserved by a team for a period of time, usually for a scrimmage.
 */
public class ReservedSlot extends AbstractResponse<ReservedSlot> {

  public ReservedSlot(JsonObject fields) throws SchemaViolationError {
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

        case "end_at": {
          DateTime optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = DateTime.parse(jsonAsString(field.getValue(), key));
          }

          responseData.put(key, optional1);

          break;
        }

        case "reservee": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
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

        case "start_at": {
          DateTime optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = DateTime.parse(jsonAsString(field.getValue(), key));
          }

          responseData.put(key, optional1);

          break;
        }

        case "team_id": {
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
    return "ReservedSlot";
  }

  /**
   * Date when this reservedslot was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public ReservedSlot setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * When the reservation ends.
   */

  public DateTime getEndAt() {
    return (DateTime) get("end_at");
  }

  public ReservedSlot setEndAt(DateTime arg) {
    optimisticData.put(getKey("end_at"), arg);
    return this;
  }

  /**
   * ID of the user who made the reservation.
   */

  public Integer getReservee() {
    return (Integer) get("reservee");
  }

  public ReservedSlot setReservee(Integer arg) {
    optimisticData.put(getKey("reservee"), arg);
    return this;
  }

  /**
   * Name of the server which is reserved.
   */

  public String getServer() {
    return (String) get("server");
  }

  public ReservedSlot setServer(String arg) {
    optimisticData.put(getKey("server"), arg);
    return this;
  }

  /**
   * When the reservation starts.
   */

  public DateTime getStartAt() {
    return (DateTime) get("start_at");
  }

  public ReservedSlot setStartAt(DateTime arg) {
    optimisticData.put(getKey("start_at"), arg);
    return this;
  }

  /**
   * ID of the team that owns the server.
   */

  public Integer getTeamId() {
    return (Integer) get("team_id");
  }

  public ReservedSlot setTeamId(Integer arg) {
    optimisticData.put(getKey("team_id"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "created_at":
        return false;

      case "end_at":
        return false;

      case "reservee":
        return false;

      case "server":
        return false;

      case "start_at":
        return false;

      case "team_id":
        return false;

      default:
        return false;
    }
  }
}
