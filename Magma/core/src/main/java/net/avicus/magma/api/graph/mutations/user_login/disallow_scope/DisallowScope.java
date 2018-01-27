package net.avicus.magma.api.graph.mutations.user_login.disallow_scope;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import net.avicus.magma.api.graph.types.punishment.Punishment;

/**
 * Information about if this login is allowed.
 */
public class DisallowScope extends AbstractResponse<DisallowScope> {

  public DisallowScope(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "permissions": {
          responseData.put(key, jsonAsBoolean(field.getValue(), key));

          break;
        }

        case "punishment": {
          responseData.put(key, jsonAsBoolean(field.getValue(), key));

          break;
        }

        case "punishment_data": {
          Punishment optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = new Punishment(jsonAsObject(field.getValue(), key));
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
    return "DisallowScope";
  }

  /**
   * If the login is disallowed due to lack of permissions.
   */

  public Boolean isPermissions() {
    return (Boolean) get("permissions");
  }

  public DisallowScope setPermissions(Boolean arg) {
    optimisticData.put(getKey("permissions"), arg);
    return this;
  }

  /**
   * If the login is disallowed due to a punishment.
   */

  public Boolean isPunishment() {
    return (Boolean) get("punishment");
  }

  public DisallowScope setPunishment(Boolean arg) {
    optimisticData.put(getKey("punishment"), arg);
    return this;
  }

  /**
   * The punishment that disallowed the login.
   */

  public Punishment getPunishmentData() {
    return (Punishment) get("punishment_data");
  }

  public DisallowScope setPunishmentData(Punishment arg) {
    optimisticData.put(getKey("punishment_data"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "permissions":
        return false;

      case "punishment":
        return false;

      case "punishment_data":
        return true;

      default:
        return false;
    }
  }
}
