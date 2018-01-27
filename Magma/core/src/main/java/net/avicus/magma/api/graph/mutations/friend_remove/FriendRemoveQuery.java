package net.avicus.magma.api.graph.mutations.friend_remove;

import com.shopify.graphql.support.Query;
import net.avicus.magma.api.graph.mutations.friend_remove.remove_response_data.RemoveResponseDataQuery;
import net.avicus.magma.api.graph.mutations.friend_remove.remove_response_data.RemoveResponseDataQueryDefinition;

public class FriendRemoveQuery extends Query<FriendRemoveQuery> {

  public FriendRemoveQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * A unique identifier for the client performing the mutation.
   */
  public FriendRemoveQuery clientMutationId() {
    startField("clientMutationId");

    return this;
  }

  /**
   * Information about what the request accompanied (removed, request canceled, not friends, etc).
   */
  public FriendRemoveQuery responseData(RemoveResponseDataQueryDefinition queryDef) {
    startField("response_data");

    builder().append('{');
    queryDef.define(new RemoveResponseDataQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * If the friend was removed.
   */
  public FriendRemoveQuery success() {
    startField("success");

    return this;
  }
}
