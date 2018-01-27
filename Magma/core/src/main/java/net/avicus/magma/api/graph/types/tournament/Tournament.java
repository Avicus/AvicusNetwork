package net.avicus.magma.api.graph.types.tournament;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * An event which teams can register for and play in.
 */
public class Tournament extends AbstractResponse<Tournament> {

  public Tournament(JsonObject fields) throws SchemaViolationError {
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

        case "allow_loners": {
          Boolean optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsBoolean(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "close_at": {
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

        case "header": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "max": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "min": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
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

        case "open_at": {
          DateTime optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = DateTime.parse(jsonAsString(field.getValue(), key));
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
    return "Tournament";
  }

  /**
   * Raw HTML of the about section of the tournament.
   */

  public String getAbout() {
    return (String) get("about");
  }

  public Tournament setAbout(String arg) {
    optimisticData.put(getKey("about"), arg);
    return this;
  }

  /**
   * If this tournament allows individual users to register.
   */

  public Boolean isAllowLoners() {
    return (Boolean) get("allow_loners");
  }

  public Tournament setAllowLoners(Boolean arg) {
    optimisticData.put(getKey("allow_loners"), arg);
    return this;
  }

  /**
   * Time when registration closes.
   */

  public DateTime getCloseAt() {
    return (DateTime) get("close_at");
  }

  public Tournament setCloseAt(DateTime arg) {
    optimisticData.put(getKey("close_at"), arg);
    return this;
  }

  /**
   * Date when this tournament was created.
   */

  public DateTime getCreatedAt() {
    return (DateTime) get("created_at");
  }

  public Tournament setCreatedAt(DateTime arg) {
    optimisticData.put(getKey("created_at"), arg);
    return this;
  }

  /**
   * If the tournament header should be shown in the UI.
   */

  public Integer getHeader() {
    return (Integer) get("header");
  }

  public Tournament setHeader(Integer arg) {
    optimisticData.put(getKey("header"), arg);
    return this;
  }

  /**
   * Maximum number of players allowed to play for each team.
   */

  public Integer getMax() {
    return (Integer) get("max");
  }

  public Tournament setMax(Integer arg) {
    optimisticData.put(getKey("max"), arg);
    return this;
  }

  /**
   * Minimum number of players allowed to play for each team.
   */

  public Integer getMin() {
    return (Integer) get("min");
  }

  public Tournament setMin(Integer arg) {
    optimisticData.put(getKey("min"), arg);
    return this;
  }

  /**
   * The name of the tournament
   */

  public String getName() {
    return (String) get("name");
  }

  public Tournament setName(String arg) {
    optimisticData.put(getKey("name"), arg);
    return this;
  }

  /**
   * Time when registration opens.
   */

  public DateTime getOpenAt() {
    return (DateTime) get("open_at");
  }

  public Tournament setOpenAt(DateTime arg) {
    optimisticData.put(getKey("open_at"), arg);
    return this;
  }

  /**
   * The slug of the tournament used in the URL.
   */

  public String getSlug() {
    return (String) get("slug");
  }

  public Tournament setSlug(String arg) {
    optimisticData.put(getKey("slug"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "about":
        return false;

      case "allow_loners":
        return false;

      case "close_at":
        return false;

      case "created_at":
        return false;

      case "header":
        return false;

      case "max":
        return false;

      case "min":
        return false;

      case "name":
        return false;

      case "open_at":
        return false;

      case "slug":
        return false;

      default:
        return false;
    }
  }
}
