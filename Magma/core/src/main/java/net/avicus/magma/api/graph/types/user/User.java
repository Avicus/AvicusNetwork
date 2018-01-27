package net.avicus.magma.api.graph.types.user;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * A person who has logged into the server at least once.
 */
public class User extends AbstractResponse<User> {

  public User(JsonObject fields) throws SchemaViolationError {
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

        case "discord_id": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "locale": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "mc_version": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "tracker": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "username": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "uuid": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "verify_key": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "verify_key_success": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
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
    return "User";
  }

  /**
   * Date when this user was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public User setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * The discord client ID of the user.
   */

  public Integer getDiscordId() {
    return (Integer) get("discord_id");
  }

  public User setDiscordId(Integer arg) {
    optimisticData.put(getKey("discord_id"), arg);
    return this;
  }

  /**
   * The Minecrft locale the user had set when they last logged in.
   */

  public String getLocale() {
    return (String) get("locale");
  }

  public User setLocale(String arg) {
    optimisticData.put(getKey("locale"), arg);
    return this;
  }

  /**
   * The last version of Minecraft the user logged in with.
   */

  public Integer getMcVersion() {
    return (Integer) get("mc_version");
  }

  public User setMcVersion(Integer arg) {
    optimisticData.put(getKey("mc_version"), arg);
    return this;
  }

  /**
   * Tracking information for the user.
   */

  public String getTracker() {
    return (String) get("tracker");
  }

  public User setTracker(String arg) {
    optimisticData.put(getKey("tracker"), arg);
    return this;
  }

  /**
   * The username of the user when they last logged in.
   */

  public String getUsername() {
    return (String) get("username");
  }

  public User setUsername(String arg) {
    optimisticData.put(getKey("username"), arg);
    return this;
  }

  /**
   * The Minecraft UUID of the user.
   */

  public String getUuid() {
    return (String) get("uuid");
  }

  public User setUuid(String arg) {
    optimisticData.put(getKey("uuid"), arg);
    return this;
  }

  /**
   * The verification key assigned to the user during registration.
   */

  public String getVerifyKey() {
    return (String) get("verify_key");
  }

  public User setVerifyKey(String arg) {
    optimisticData.put(getKey("verify_key"), arg);
    return this;
  }

  /**
   * If the user successfullly verified their identitity with the server during registration.
   */

  public Boolean isVerifyKeySuccess() {
    return (Boolean) get("verify_key_success");
  }

  public User setVerifyKeySuccess(Boolean arg) {
    optimisticData.put(getKey("verify_key_success"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "created_at":
        return false;

      case "discord_id":
        return false;

      case "locale":
        return false;

      case "mc_version":
        return false;

      case "tracker":
        return false;

      case "username":
        return false;

      case "uuid":
        return false;

      case "verify_key":
        return false;

      case "verify_key_success":
        return false;

      default:
        return false;
    }
  }
}
