package net.avicus.magma.api.graph.types.rank;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * A group of users who are assigned special properties.
 */
public class Rank extends AbstractResponse<Rank> {

  public Rank(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "badge_color": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "badge_text_color": {
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

        case "html_color": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "inheritance_id": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "is_staff": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "mc_perms": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "mc_prefix": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "mc_suffix": {
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

        case "priority": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "ts_perms": {
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
    return "Rank";
  }

  /**
   * Background color of the badge given to users who have this rank on the website.
   */

  public String getBadgeColor() {
    return (String) get("badge_color");
  }

  public Rank setBadgeColor(String arg) {
    optimisticData.put(getKey("badge_color"), arg);
    return this;
  }

  /**
   * Color of the text in the badge given to users who have this rank on the website.
   */

  public String getBadgeTextColor() {
    return (String) get("badge_text_color");
  }

  public Rank setBadgeTextColor(String arg) {
    optimisticData.put(getKey("badge_text_color"), arg);
    return this;
  }

  /**
   * Date when this rank was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public Rank setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * Color of usernames of users who are in this rank on the website.
   */

  public String getHtmlColor() {
    return (String) get("html_color");
  }

  public Rank setHtmlColor(String arg) {
    optimisticData.put(getKey("html_color"), arg);
    return this;
  }

  /**
   * ID of the rank which this one inherits permissions from.
   */

  public Integer getInheritanceId() {
    return (Integer) get("inheritance_id");
  }

  public Rank setInheritanceId(Integer arg) {
    optimisticData.put(getKey("inheritance_id"), arg);
    return this;
  }

  /**
   * If the users inside of this rank should be marked as staff.
   */

  public Boolean isStaff() {
    return (Boolean) get("is_staff");
  }

  public Rank setIsStaff(Boolean arg) {
    optimisticData.put(getKey("is_staff"), arg);
    return this;
  }

  /**
   * List of permissions given to any user in the rank across the network.
   */

  public String getMcPerms() {
    return (String) get("mc_perms");
  }

  public Rank setMcPerms(String arg) {
    optimisticData.put(getKey("mc_perms"), arg);
    return this;
  }

  /**
   * Prefix before users who have this rank in game.
   */

  public String getMcPrefix() {
    return (String) get("mc_prefix");
  }

  public Rank setMcPrefix(String arg) {
    optimisticData.put(getKey("mc_prefix"), arg);
    return this;
  }

  /**
   * Suffix after users who have this rank in game
   */

  public String getMcSuffix() {
    return (String) get("mc_suffix");
  }

  public Rank setMcSuffix(String arg) {
    optimisticData.put(getKey("mc_suffix"), arg);
    return this;
  }

  /**
   * The name of the rank.
   */

  public String getName() {
    return (String) get("name");
  }

  public Rank setName(String arg) {
    optimisticData.put(getKey("name"), arg);
    return this;
  }

  /**
   * Sort order of this rank when being used to determine the color/prefix/suffix a user should
   * receive.
   */

  public Integer getPriority() {
    return (Integer) get("priority");
  }

  public Rank setPriority(Integer arg) {
    optimisticData.put(getKey("priority"), arg);
    return this;
  }

  /**
   * Permissions given to users who have this rank on TeamSpeak
   */

  public String getTsPerms() {
    return (String) get("ts_perms");
  }

  public Rank setTsPerms(String arg) {
    optimisticData.put(getKey("ts_perms"), arg);
    return this;
  }

  /**
   * Date when this rank was last updated.
   */

  public DateTime getUpdatedAt() {
    return (DateTime) get("updated_at");
  }

  public Rank setUpdatedAt(DateTime arg) {
    optimisticData.put(getKey("updated_at"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "badge_color":
        return false;

      case "badge_text_color":
        return false;

      case "created_at":
        return false;

      case "html_color":
        return false;

      case "inheritance_id":
        return false;

      case "is_staff":
        return false;

      case "mc_perms":
        return false;

      case "mc_prefix":
        return false;

      case "mc_suffix":
        return false;

      case "name":
        return false;

      case "priority":
        return false;

      case "ts_perms":
        return false;

      case "updated_at":
        return false;

      default:
        return false;
    }
  }
}
