package net.avicus.magma.api.graph.mutations;

import com.shopify.graphql.support.Query;
import net.avicus.magma.api.graph.inputs.AlertDeleteInput;
import net.avicus.magma.api.graph.inputs.AlertSendInput;
import net.avicus.magma.api.graph.inputs.FriendAddInput;
import net.avicus.magma.api.graph.inputs.FriendRemoveInput;
import net.avicus.magma.api.graph.inputs.GadgetPurchaseInput;
import net.avicus.magma.api.graph.inputs.GadgetUpdateInput;
import net.avicus.magma.api.graph.inputs.PresentFindInput;
import net.avicus.magma.api.graph.inputs.UserLoginInput;
import net.avicus.magma.api.graph.mutations.alert_delete.AlertDeleteQuery;
import net.avicus.magma.api.graph.mutations.alert_delete.AlertDeleteQueryDefinition;
import net.avicus.magma.api.graph.mutations.alert_send.AlertSendQuery;
import net.avicus.magma.api.graph.mutations.alert_send.AlertSendQueryDefinition;
import net.avicus.magma.api.graph.mutations.friend_add.FriendAddQuery;
import net.avicus.magma.api.graph.mutations.friend_add.FriendAddQueryDefinition;
import net.avicus.magma.api.graph.mutations.friend_remove.FriendRemoveQuery;
import net.avicus.magma.api.graph.mutations.friend_remove.FriendRemoveQueryDefinition;
import net.avicus.magma.api.graph.mutations.gadget_purchase.GadgetPurchaseQuery;
import net.avicus.magma.api.graph.mutations.gadget_purchase.GadgetPurchaseQueryDefinition;
import net.avicus.magma.api.graph.mutations.gadget_update.GadgetUpdateQuery;
import net.avicus.magma.api.graph.mutations.gadget_update.GadgetUpdateQueryDefinition;
import net.avicus.magma.api.graph.mutations.present_find.PresentFindQuery;
import net.avicus.magma.api.graph.mutations.present_find.PresentFindQueryDefinition;
import net.avicus.magma.api.graph.mutations.user_login.UserLoginQuery;
import net.avicus.magma.api.graph.mutations.user_login.UserLoginQueryDefinition;

public class MutationQuery extends Query<MutationQuery> {

  public MutationQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Delete an alert.
   */
  public MutationQuery alertDelete(AlertDeleteInput input, AlertDeleteQueryDefinition queryDef) {
    startField("alertDelete");

    builder().append("(input:");
    input.appendTo(builder());

    builder().append(')');

    builder().append('{');
    queryDef.define(new AlertDeleteQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Send a user an alert.
   */
  public MutationQuery alertSend(AlertSendInput input, AlertSendQueryDefinition queryDef) {
    startField("alertSend");

    builder().append("(input:");
    input.appendTo(builder());

    builder().append(')');

    builder().append('{');
    queryDef.define(new AlertSendQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * When a user attempts to add another user as a friend.
   */
  public MutationQuery friendAdd(FriendAddInput input, FriendAddQueryDefinition queryDef) {
    startField("friendAdd");

    builder().append("(input:");
    input.appendTo(builder());

    builder().append(')');

    builder().append('{');
    queryDef.define(new FriendAddQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * When a user wants to remove another user from their friends list.
   */
  public MutationQuery friendRemove(FriendRemoveInput input, FriendRemoveQueryDefinition queryDef) {
    startField("friendRemove");

    builder().append("(input:");
    input.appendTo(builder());

    builder().append(')');

    builder().append('{');
    queryDef.define(new FriendRemoveQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * When a gadget is attempting to be purchased by a user.
   */
  public MutationQuery gadgetPurchase(GadgetPurchaseInput input,
      GadgetPurchaseQueryDefinition queryDef) {
    startField("gadgetPurchase");

    builder().append("(input:");
    input.appendTo(builder());

    builder().append(')');

    builder().append('{');
    queryDef.define(new GadgetPurchaseQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * When a gadget is used by a user, returned usages remaining and if the gadget should be removed.
   */
  public MutationQuery gadgetUpdate(GadgetUpdateInput input, GadgetUpdateQueryDefinition queryDef) {
    startField("gadgetUpdate");

    builder().append("(input:");
    input.appendTo(builder());

    builder().append(')');

    builder().append('{');
    queryDef.define(new GadgetUpdateQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * When a user finds a present in a lobby.
   */
  public MutationQuery presentFind(PresentFindInput input, PresentFindQueryDefinition queryDef) {
    startField("presentFind");

    builder().append("(input:");
    input.appendTo(builder());

    builder().append(')');

    builder().append('{');
    queryDef.define(new PresentFindQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * When a user logs into a server, will create a new user if one does not exist.
   */
  public MutationQuery userLogin(UserLoginInput input, UserLoginQueryDefinition queryDef) {
    startField("userLogin");

    builder().append("(input:");
    input.appendTo(builder());

    builder().append(')');

    builder().append('{');
    queryDef.define(new UserLoginQuery(builder()));
    builder().append('}');

    return this;
  }
}
