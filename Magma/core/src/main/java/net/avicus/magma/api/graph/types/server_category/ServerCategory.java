package net.avicus.magma.api.graph.types.server_category;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;

/**
 * A group of servers which share the same general category.
 */
public class ServerCategory extends AbstractResponse<ServerCategory> {

  public ServerCategory(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "communication_options": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "infraction_options": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "name": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "tracking_options": {
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
    return "ServerCategory";
  }

  /**
   * Options related to how servers in this category should communicate both outward and inside the
   * category.
   */

  public String getCommunicationOptions() {
    return (String) get("communication_options");
  }

  public ServerCategory setCommunicationOptions(String arg) {
    optimisticData.put(getKey("communication_options"), arg);
    return this;
  }

  /**
   * Options related to the tracking/enforcement of
   */

  public String getInfractionOptions() {
    return (String) get("infraction_options");
  }

  public ServerCategory setInfractionOptions(String arg) {
    optimisticData.put(getKey("infraction_options"), arg);
    return this;
  }

  /**
   * Name of the category.
   */

  public String getName() {
    return (String) get("name");
  }

  public ServerCategory setName(String arg) {
    optimisticData.put(getKey("name"), arg);
    return this;
  }

  /**
   * Options related to the tracking of stats.
   */

  public String getTrackingOptions() {
    return (String) get("tracking_options");
  }

  public ServerCategory setTrackingOptions(String arg) {
    optimisticData.put(getKey("tracking_options"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "communication_options":
        return false;

      case "infraction_options":
        return false;

      case "name":
        return false;

      case "tracking_options":
        return false;

      default:
        return false;
    }
  }
}
