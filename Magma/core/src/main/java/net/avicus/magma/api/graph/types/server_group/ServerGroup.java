package net.avicus.magma.api.graph.types.server_group;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * A group of servers inside of a category.
 */
public class ServerGroup extends AbstractResponse<ServerGroup> {

  public ServerGroup(JsonObject fields) throws SchemaViolationError {
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

        case "data": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "description": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "icon": {
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

        case "slug": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
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
    return "ServerGroup";
  }

  /**
   * Date when this servergroup was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public ServerGroup setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * General data for this group.
   */

  public String getData() {
    return (String) get("data");
  }

  public ServerGroup setData(String arg) {
    optimisticData.put(getKey("data"), arg);
    return this;
  }

  /**
   * Description of the group used in UI.
   */

  public String getDescription() {
    return (String) get("description");
  }

  public ServerGroup setDescription(String arg) {
    optimisticData.put(getKey("description"), arg);
    return this;
  }

  /**
   * Icon for the server picker
   */

  public String getIcon() {
    return (String) get("icon");
  }

  public ServerGroup setIcon(String arg) {
    optimisticData.put(getKey("icon"), arg);
    return this;
  }

  /**
   * Name of the group.
   */

  public String getName() {
    return (String) get("name");
  }

  public ServerGroup setName(String arg) {
    optimisticData.put(getKey("name"), arg);
    return this;
  }

  /**
   * The slug of group.
   */

  public String getSlug() {
    return (String) get("slug");
  }

  public ServerGroup setSlug(String arg) {
    optimisticData.put(getKey("slug"), arg);
    return this;
  }

  /**
   * Date when this servergroup was last updated.
   */

  public DateTime getUpdatedAt() {
    return (DateTime) get("updated_at");
  }

  public ServerGroup setUpdatedAt(DateTime arg) {
    optimisticData.put(getKey("updated_at"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "created_at":
        return false;

      case "data":
        return false;

      case "description":
        return false;

      case "icon":
        return false;

      case "name":
        return false;

      case "slug":
        return false;

      case "updated_at":
        return false;

      default:
        return false;
    }
  }
}
