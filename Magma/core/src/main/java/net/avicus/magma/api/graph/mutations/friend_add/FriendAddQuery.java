package net.avicus.magma.api.graph.mutations.friend_add;

import com.shopify.graphql.support.Query;
import net.avicus.magma.api.graph.mutations.friend_remove.remove_response_data.RemoveResponseDataQuery;
import net.avicus.magma.api.graph.mutations.friend_remove.remove_response_data.RemoveResponseDataQueryDefinition;

public class FriendAddQuery extends Query<FriendAddQuery> {

  public FriendAddQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * A unique identifier for the client performing the mutation.
   */
  public FriendAddQuery clientMutationId() {
    startField("clientMutationId");

    return this;
  }

  /**
   * Information about what the request accompanied (added, friend, already friends, etc).
   */
  public FriendAddQuery responseData(RemoveResponseDataQueryDefinition queryDef) {
    startField("response_data");

    builder().append('{');
    queryDef.define(new RemoveResponseDataQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * If the friend request was sent/accepted.
   */
  public FriendAddQuery success() {
    startField("success");

    return this;
  }
}
