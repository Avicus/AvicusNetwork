package net.avicus.magma.api.graph.mutations.user_login.chat_data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;

/**
 * Information about the user's display in chat.
 */
public class ChatData extends AbstractResponse<ChatData> {

  public ChatData(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "prefix": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "suffix": {
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
    return "ChatData";
  }

  /**
   * Prefix that the user should have in game.
   */

  public String getPrefix() {
    return (String) get("prefix");
  }

  public ChatData setPrefix(String arg) {
    optimisticData.put(getKey("prefix"), arg);
    return this;
  }

  /**
   * Suffix that the user should have in game.
   */

  public String getSuffix() {
    return (String) get("suffix");
  }

  public ChatData setSuffix(String arg) {
    optimisticData.put(getKey("suffix"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "prefix":
        return false;

      case "suffix":
        return false;

      default:
        return false;
    }
  }
}
