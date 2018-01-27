package net.avicus.magma.api.graph.types.team;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * A group of people which can participate in tournaments and reserve servers.
 */
public class Team extends AbstractResponse<Team> {

  public Team(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "about": {
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

        case "tag": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "tagline": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "title": {
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
    return "Team";
  }

  /**
   * The about section on the team page in raw HTML.
   */

  public String getAbout() {
    return (String) get("about");
  }

  public Team setAbout(String arg) {
    optimisticData.put(getKey("about"), arg);
    return this;
  }

  /**
   * Date when this team was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public Team setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * The tag of the team.
   */

  public String getTag() {
    return (String) get("tag");
  }

  public Team setTag(String arg) {
    optimisticData.put(getKey("tag"), arg);
    return this;
  }

  /**
   * The tagline of the team.
   */

  public String getTagline() {
    return (String) get("tagline");
  }

  public Team setTagline(String arg) {
    optimisticData.put(getKey("tagline"), arg);
    return this;
  }

  /**
   * The title of the team.
   */

  public String getTitle() {
    return (String) get("title");
  }

  public Team setTitle(String arg) {
    optimisticData.put(getKey("title"), arg);
    return this;
  }

  /**
   * Date when this team was last updated.
   */

  public DateTime getUpdatedAt() {
    return (DateTime) get("updated_at");
  }

  public Team setUpdatedAt(DateTime arg) {
    optimisticData.put(getKey("updated_at"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "about":
        return false;

      case "created_at":
        return false;

      case "tag":
        return false;

      case "tagline":
        return false;

      case "title":
        return false;

      case "updated_at":
        return false;

      default:
        return false;
    }
  }
}
