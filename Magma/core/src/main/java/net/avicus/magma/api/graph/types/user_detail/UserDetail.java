package net.avicus.magma.api.graph.types.user_detail;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;

/**
 * A set of information about a user which is diaplyed on their profile.
 */
public class UserDetail extends AbstractResponse<UserDetail> {

  public UserDetail(JsonObject fields) throws SchemaViolationError {
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

        case "avatar": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "cover_art": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "email": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "email_status": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "facebook": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "gender": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "github": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "instagram": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "interests": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "skype": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "steam": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "twitch": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "twitter": {
          String optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsString(field.getValue(), key);
          }

          responseData.put(key, optional1);

          break;
        }

        case "user_id": {
          Integer optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = jsonAsInteger(field.getValue(), key);
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
    return "UserDetail";
  }

  /**
   * Raw HTML of the about page text of the user.
   */

  public String getAbout() {
    return (String) get("about");
  }

  public UserDetail setAbout(String arg) {
    optimisticData.put(getKey("about"), arg);
    return this;
  }

  /**
   * Gravatar ID of the user.
   */

  public String getAvatar() {
    return (String) get("avatar");
  }

  public UserDetail setAvatar(String arg) {
    optimisticData.put(getKey("avatar"), arg);
    return this;
  }

  /**
   * Path to the cover art on the profile.
   */

  public String getCoverArt() {
    return (String) get("cover_art");
  }

  public UserDetail setCoverArt(String arg) {
    optimisticData.put(getKey("cover_art"), arg);
    return this;
  }

  /**
   * Email of the user used for gravatar
   */

  public String getEmail() {
    return (String) get("email");
  }

  public UserDetail setEmail(String arg) {
    optimisticData.put(getKey("email"), arg);
    return this;
  }

  /**
   * If the user has confirmed their email.
   */

  public Integer getEmailStatus() {
    return (Integer) get("email_status");
  }

  public UserDetail setEmailStatus(Integer arg) {
    optimisticData.put(getKey("email_status"), arg);
    return this;
  }

  /**
   * Facebook name of the user
   */

  public String getFacebook() {
    return (String) get("facebook");
  }

  public UserDetail setFacebook(String arg) {
    optimisticData.put(getKey("facebook"), arg);
    return this;
  }

  /**
   * Gender of the user.
   */

  public String getGender() {
    return (String) get("gender");
  }

  public UserDetail setGender(String arg) {
    optimisticData.put(getKey("gender"), arg);
    return this;
  }

  /**
   * Github username of the user.
   */

  public String getGithub() {
    return (String) get("github");
  }

  public UserDetail setGithub(String arg) {
    optimisticData.put(getKey("github"), arg);
    return this;
  }

  /**
   * Instagram handle of the user.
   */

  public String getInstagram() {
    return (String) get("instagram");
  }

  public UserDetail setInstagram(String arg) {
    optimisticData.put(getKey("instagram"), arg);
    return this;
  }

  /**
   * List of things the user is interested in.
   */

  public String getInterests() {
    return (String) get("interests");
  }

  public UserDetail setInterests(String arg) {
    optimisticData.put(getKey("interests"), arg);
    return this;
  }

  /**
   * Skype username of the user.
   */

  public String getSkype() {
    return (String) get("skype");
  }

  public UserDetail setSkype(String arg) {
    optimisticData.put(getKey("skype"), arg);
    return this;
  }

  /**
   * Steam ID of the user.
   */

  public String getSteam() {
    return (String) get("steam");
  }

  public UserDetail setSteam(String arg) {
    optimisticData.put(getKey("steam"), arg);
    return this;
  }

  /**
   * Twitch username of the user.
   */

  public String getTwitch() {
    return (String) get("twitch");
  }

  public UserDetail setTwitch(String arg) {
    optimisticData.put(getKey("twitch"), arg);
    return this;
  }

  /**
   * Twitter handle of the user.
   */

  public String getTwitter() {
    return (String) get("twitter");
  }

  public UserDetail setTwitter(String arg) {
    optimisticData.put(getKey("twitter"), arg);
    return this;
  }

  /**
   * Id of the user that these details represent.
   */

  public Integer getUserId() {
    return (Integer) get("user_id");
  }

  public UserDetail setUserId(Integer arg) {
    optimisticData.put(getKey("user_id"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "about":
        return false;

      case "avatar":
        return false;

      case "cover_art":
        return false;

      case "email":
        return false;

      case "email_status":
        return false;

      case "facebook":
        return false;

      case "gender":
        return false;

      case "github":
        return false;

      case "instagram":
        return false;

      case "interests":
        return false;

      case "skype":
        return false;

      case "steam":
        return false;

      case "twitch":
        return false;

      case "twitter":
        return false;

      case "user_id":
        return false;

      default:
        return false;
    }
  }
}
