package net.avicus.magma.api.graph.mutations.user_login.chat_data;

import com.shopify.graphql.support.Query;

public class ChatDataQuery extends Query<ChatDataQuery> {

  public ChatDataQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Prefix that the user should have in game.
   */
  public ChatDataQuery prefix() {
    startField("prefix");

    return this;
  }

  /**
   * Suffix that the user should have in game.
   */
  public ChatDataQuery suffix() {
    startField("suffix");

    return this;
  }
}
