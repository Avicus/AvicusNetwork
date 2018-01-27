package net.avicus.magma.api.graph.types.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * A server which users can join.
 */
public class Server extends AbstractResponse<Server> {

  public Server(JsonObject fields) throws SchemaViolationError {
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

        case "host": {
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

        case "permissible": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "port": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "server_category_id": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "server_group_id": {
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
    return "Server";
  }

  /**
   * Date when this server was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public Server setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * Host of the box which this server is hosted on.
   */

  public String getHost() {
    return (String) get("host");
  }

  public Server setHost(String arg) {
    optimisticData.put(getKey("host"), arg);
    return this;
  }

  /**
   * Name of the server.
   */

  public String getName() {
    return (String) get("name");
  }

  public Server setName(String arg) {
    optimisticData.put(getKey("name"), arg);
    return this;
  }

  /**
   * If the server can only be joined by users with the correct permission.
   */

  public Boolean isPermissible() {
    return (Boolean) get("permissible");
  }

  public Server setPermissible(Boolean arg) {
    optimisticData.put(getKey("permissible"), arg);
    return this;
  }

  /**
   * Port of the server.
   */

  public Integer getPort() {
    return (Integer) get("port");
  }

  public Server setPort(Integer arg) {
    optimisticData.put(getKey("port"), arg);
    return this;
  }

  /**
   * ID of the server category this server belongs to.
   */

  public Integer getServerCategoryId() {
    return (Integer) get("server_category_id");
  }

  public Server setServerCategoryId(Integer arg) {
    optimisticData.put(getKey("server_category_id"), arg);
    return this;
  }

  /**
   * ID of the server group this server belongs to.
   */

  public Integer getServerGroupId() {
    return (Integer) get("server_group_id");
  }

  public Server setServerGroupId(Integer arg) {
    optimisticData.put(getKey("server_group_id"), arg);
    return this;
  }

  /**
   * Date when this server was last updated.
   */

  public DateTime getUpdatedAt() {
    return (DateTime) get("updated_at");
  }

  public Server setUpdatedAt(DateTime arg) {
    optimisticData.put(getKey("updated_at"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "created_at":
        return false;

      case "host":
        return false;

      case "name":
        return false;

      case "permissible":
        return false;

      case "port":
        return false;

      case "server_category_id":
        return false;

      case "server_group_id":
        return false;

      case "updated_at":
        return false;

      default:
        return false;
    }
  }
}
