package net.avicus.magma.api.graph.types.team_member;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * Representation of a user who is apart of a team.
 */
public class TeamMember extends AbstractResponse<TeamMember> {

  public TeamMember(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "accepted": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "accepted_at": {
          DateTime optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = DateTime.parse(jsonAsString(field.getValue(), key));
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

        case "role": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
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
    return "TeamMember";
  }

  /**
   * If the user accepted the invitation to join the team.
   */

  public Integer getAccepted() {
    return (Integer) get("accepted");
  }

  public TeamMember setAccepted(Integer arg) {
    optimisticData.put(getKey("accepted"), arg);
    return this;
  }

  /**
   * Date that the user acceoted the invitation to join the team.
   */

  public DateTime getAcceptedAt() {
    return (DateTime) get("accepted_at");
  }

  public TeamMember setAcceptedAt(DateTime arg) {
    optimisticData.put(getKey("accepted_at"), arg);
    return this;
  }

  /**
   * Date when this teammember was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public TeamMember setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * Role of the user on the team
   */

  public String getRole() {
    return (String) get("role");
  }

  public TeamMember setRole(String arg) {
    optimisticData.put(getKey("role"), arg);
    return this;
  }

  /**
   * ID of the team that the user is on.
   */

  public Integer getTeamId() {
    return (Integer) get("team_id");
  }

  public TeamMember setTeamId(Integer arg) {
    optimisticData.put(getKey("team_id"), arg);
    return this;
  }

  /**
   * ID of the user who is on the team.
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public TeamMember setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "accepted":
        return false;

      case "accepted_at":
        return false;

      case "created_at":
        return false;

      case "role":
        return false;

      case "team_id":
        return false;

      case "user_id":
        return false;

      default:
        return false;
    }
  }
}
