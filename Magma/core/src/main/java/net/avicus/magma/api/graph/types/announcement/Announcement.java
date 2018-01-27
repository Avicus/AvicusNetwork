package net.avicus.magma.api.graph.types.announcement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * Something that is shown in the UI.
 */
public class Announcement extends AbstractResponse<Announcement> {

  public Announcement(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "body": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
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

        case "enabled": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "lobby": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "motd": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "motd_format": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "permission": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "popup": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "tips": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
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

        case "web": {
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
    return "Announcement";
  }

  /**
   * The text of the announcement.
   */

  public String getBody() {
    return (String) get("body");
  }

  public Announcement setBody(String arg) {
    optimisticData.put(getKey("body"), arg);
    return this;
  }

  /**
   * Date when this announcement was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public Announcement setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * If the announcement should be shown.
   */

  public Boolean isEnabled() {
    return (Boolean) get("enabled");
  }

  public Announcement setEnabled(Boolean arg) {
    optimisticData.put(getKey("enabled"), arg);
    return this;
  }

  /**
   * If the announcement should show in lobbies. This will be ignored if tips is also enabled.
   */

  public Boolean isLobby() {
    return (Boolean) get("lobby");
  }

  public Announcement setLobby(Boolean arg) {
    optimisticData.put(getKey("lobby"), arg);
    return this;
  }

  /**
   * If the announcement should be used for MOTDs.
   */

  public Boolean isMotd() {
    return (Boolean) get("motd");
  }

  public Announcement setMotd(Boolean arg) {
    optimisticData.put(getKey("motd"), arg);
    return this;
  }

  /**
   * If the announcement should be used as an MOTD format.
   */

  public Boolean isMotdFormat() {
    return (Boolean) get("motd_format");
  }

  public Announcement setMotdFormat(Boolean arg) {
    optimisticData.put(getKey("motd_format"), arg);
    return this;
  }

  /**
   * A minecraft permission needed to view the announcement in game.
   */

  public String getPermission() {
    return (String) get("permission");
  }

  public Announcement setPermission(String arg) {
    optimisticData.put(getKey("permission"), arg);
    return this;
  }

  /**
   * If the announcement should be displayed as a title when a user joins a lobby,
   */

  public Boolean isPopup() {
    return (Boolean) get("popup");
  }

  public Announcement setPopup(Boolean arg) {
    optimisticData.put(getKey("popup"), arg);
    return this;
  }

  /**
   * If the announcement should be shown periodically in game.
   */

  public Boolean isTips() {
    return (Boolean) get("tips");
  }

  public Announcement setTips(Boolean arg) {
    optimisticData.put(getKey("tips"), arg);
    return this;
  }

  /**
   * Date when this announcement was last updated.
   */

  public DateTime getUpdatedAt() {
    return (DateTime) get("updated_at");
  }

  public Announcement setUpdatedAt(DateTime arg) {
    optimisticData.put(getKey("updated_at"), arg);
    return this;
  }

  /**
   * If the announcement should be displayed at the top of the website.
   */

  public Boolean isWeb() {
    return (Boolean) get("web");
  }

  public Announcement setWeb(Boolean arg) {
    optimisticData.put(getKey("web"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "body":
        return false;

      case "created_at":
        return false;

      case "enabled":
        return false;

      case "lobby":
        return false;

      case "motd":
        return false;

      case "motd_format":
        return false;

      case "permission":
        return false;

      case "popup":
        return false;

      case "tips":
        return false;

      case "updated_at":
        return false;

      case "web":
        return false;

      default:
        return false;
    }
  }
}
