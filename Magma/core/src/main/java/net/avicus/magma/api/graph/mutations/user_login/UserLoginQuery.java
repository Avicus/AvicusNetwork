package net.avicus.magma.api.graph.mutations.user_login;

import com.shopify.graphql.support.Query;
import net.avicus.magma.api.graph.mutations.user_login.chat_data.ChatDataQuery;
import net.avicus.magma.api.graph.mutations.user_login.chat_data.ChatDataQueryDefinition;
import net.avicus.magma.api.graph.mutations.user_login.disallow_scope.DisallowScopeQuery;
import net.avicus.magma.api.graph.mutations.user_login.disallow_scope.DisallowScopeQueryDefinition;
import net.avicus.magma.api.graph.types.backpack_gadget.BackpackGadgetQuery;
import net.avicus.magma.api.graph.types.backpack_gadget.BackpackGadgetQueryDefinition;
import net.avicus.magma.api.graph.types.setting.SettingQuery;
import net.avicus.magma.api.graph.types.setting.SettingQueryDefinition;

public class UserLoginQuery extends Query<UserLoginQuery> {

  public UserLoginQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Information about the user's display in chat.
   */
  public UserLoginQuery chatData(ChatDataQueryDefinition queryDef) {
    startField("chat_data");

    builder().append('{');
    queryDef.define(new ChatDataQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * A unique identifier for the client performing the mutation.
   */
  public UserLoginQuery clientMutationId() {
    startField("clientMutationId");

    return this;
  }

  /**
   * Information about if this login is allowed.
   */
  public UserLoginQuery disallowScope(DisallowScopeQueryDefinition queryDef) {
    startField("disallow_scope");

    builder().append('{');
    queryDef.define(new DisallowScopeQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Gadgets which a user has in their backpack.
   */
  public UserLoginQuery gadgets(BackpackGadgetQueryDefinition queryDef) {
    startField("gadgets");

    builder().append('{');
    queryDef.define(new BackpackGadgetQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * If the user has web alerts which are unread.
   */
  public UserLoginQuery hasAlerts() {
    startField("has_alerts");

    return this;
  }

  /**
   * Message to be shown to the user when they join. This is usually a recent announce message.
   */
  public UserLoginQuery message() {
    startField("message");

    return this;
  }

  /**
   * Permissions that the user should receive when joining this server. These include category
   * permissions.
   */
  public UserLoginQuery permissions() {
    startField("permissions");

    return this;
  }

  /**
   * Saved settings for the user.
   */
  public UserLoginQuery settings(SettingQueryDefinition queryDef) {
    startField("settings");

    builder().append('{');
    queryDef.define(new SettingQuery(builder()));
    builder().append('}');

    return this;
  }
}
