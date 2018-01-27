package net.avicus.magma.api.graph.mutations;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;
import net.avicus.magma.api.graph.mutations.alert_delete.AlertDelete;
import net.avicus.magma.api.graph.mutations.alert_send.AlertSend;
import net.avicus.magma.api.graph.mutations.friend_add.FriendAdd;
import net.avicus.magma.api.graph.mutations.friend_remove.FriendRemove;
import net.avicus.magma.api.graph.mutations.gadget_purchase.GadgetPurchase;
import net.avicus.magma.api.graph.mutations.gadget_update.GadgetUpdate;
import net.avicus.magma.api.graph.mutations.present_find.PresentFind;
import net.avicus.magma.api.graph.mutations.user_login.UserLogin;

public class Mutation extends AbstractResponse<Mutation> {

  public Mutation(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "alertDelete": {
          AlertDelete optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = new AlertDelete(jsonAsObject(field.getValue(), key));
          }

          responseData.put(key, optional1);

          break;
        }

        case "alertSend": {
          AlertSend optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = new AlertSend(jsonAsObject(field.getValue(), key));
          }

          responseData.put(key, optional1);

          break;
        }

        case "friendAdd": {
          FriendAdd optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = new FriendAdd(jsonAsObject(field.getValue(), key));
          }

          responseData.put(key, optional1);

          break;
        }

        case "friendRemove": {
          FriendRemove optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = new FriendRemove(jsonAsObject(field.getValue(), key));
          }

          responseData.put(key, optional1);

          break;
        }

        case "gadgetPurchase": {
          GadgetPurchase optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = new GadgetPurchase(jsonAsObject(field.getValue(), key));
          }

          responseData.put(key, optional1);

          break;
        }

        case "gadgetUpdate": {
          GadgetUpdate optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = new GadgetUpdate(jsonAsObject(field.getValue(), key));
          }

          responseData.put(key, optional1);

          break;
        }

        case "presentFind": {
          PresentFind optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = new PresentFind(jsonAsObject(field.getValue(), key));
          }

          responseData.put(key, optional1);

          break;
        }

        case "userLogin": {
          UserLogin optional1 = null;
          if (!field.getValue().isJsonNull()) {
            optional1 = new UserLogin(jsonAsObject(field.getValue(), key));
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
    return "Mutation";
  }

  /**
   * Delete an alert.
   */

  public AlertDelete getAlertDelete() {
    return (AlertDelete) get("alertDelete");
  }

  public Mutation setAlertDelete(AlertDelete arg) {
    optimisticData.put(getKey("alertDelete"), arg);
    return this;
  }

  /**
   * Send a user an alert.
   */

  public AlertSend getAlertSend() {
    return (AlertSend) get("alertSend");
  }

  public Mutation setAlertSend(AlertSend arg) {
    optimisticData.put(getKey("alertSend"), arg);
    return this;
  }

  /**
   * When a user attempts to add another user as a friend.
   */

  public FriendAdd getFriendAdd() {
    return (FriendAdd) get("friendAdd");
  }

  public Mutation setFriendAdd(FriendAdd arg) {
    optimisticData.put(getKey("friendAdd"), arg);
    return this;
  }

  /**
   * When a user wants to remove another user from their friends list.
   */

  public FriendRemove getFriendRemove() {
    return (FriendRemove) get("friendRemove");
  }

  public Mutation setFriendRemove(FriendRemove arg) {
    optimisticData.put(getKey("friendRemove"), arg);
    return this;
  }

  /**
   * When a gadget is attempting to be purchased by a user.
   */

  public GadgetPurchase getGadgetPurchase() {
    return (GadgetPurchase) get("gadgetPurchase");
  }

  public Mutation setGadgetPurchase(GadgetPurchase arg) {
    optimisticData.put(getKey("gadgetPurchase"), arg);
    return this;
  }

  /**
   * When a gadget is used by a user, returned usages remaining and if the gadget should be removed.
   */

  public GadgetUpdate getGadgetUpdate() {
    return (GadgetUpdate) get("gadgetUpdate");
  }

  public Mutation setGadgetUpdate(GadgetUpdate arg) {
    optimisticData.put(getKey("gadgetUpdate"), arg);
    return this;
  }

  /**
   * When a user finds a present in a lobby.
   */

  public PresentFind getPresentFind() {
    return (PresentFind) get("presentFind");
  }

  public Mutation setPresentFind(PresentFind arg) {
    optimisticData.put(getKey("presentFind"), arg);
    return this;
  }

  /**
   * When a user logs into a server, will create a new user if one does not exist.
   */

  public UserLogin getUserLogin() {
    return (UserLogin) get("userLogin");
  }

  public Mutation setUserLogin(UserLogin arg) {
    optimisticData.put(getKey("userLogin"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "alertDelete":
        return true;

      case "alertSend":
        return true;

      case "friendAdd":
        return true;

      case "friendRemove":
        return true;

      case "gadgetPurchase":
        return true;

      case "gadgetUpdate":
        return true;

      case "presentFind":
        return true;

      case "userLogin":
        return true;

      default:
        return false;
    }
  }
}
