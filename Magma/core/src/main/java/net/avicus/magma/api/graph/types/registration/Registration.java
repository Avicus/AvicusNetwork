package net.avicus.magma.api.graph.types.registration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;

/**
 * A team's registration attempt for a tournament.
 */
public class Registration extends AbstractResponse<Registration> {

  public Registration(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "status": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
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

        case "tournament_id": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "user_data": {
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
    return "Registration";
  }

  /**
   * If this registration has been accepted by a tournament staff member.
   */

  public Integer getStatus() {
    return (Integer) get("status");
  }

  public Registration setStatus(Integer arg) {
    optimisticData.put(getKey("status"), arg);
    return this;
  }

  /**
   * ID of the team who is attempting to register.
   */

  public Integer getTeamId() {
    return (Integer) get("team_id");
  }

  public Registration setTeamId(Integer arg) {
    optimisticData.put(getKey("team_id"), arg);
    return this;
  }

  /**
   * ID of the tournament which this registration is for.
   */

  public Integer getTournamentId() {
    return (Integer) get("tournament_id");
  }

  public Registration setTournamentId(Integer arg) {
    optimisticData.put(getKey("tournament_id"), arg);
    return this;
  }

  /**
   * Data about which users (denoted by ID) have accepted the invite.
   */

  public String getUserData() {
    return (String) get("user_data");
  }

  public Registration setUserData(String arg) {
    optimisticData.put(getKey("user_data"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "status":
        return false;

      case "team_id":
        return false;

      case "tournament_id":
        return false;

      case "user_data":
        return false;

      default:
        return false;
    }
  }
}
