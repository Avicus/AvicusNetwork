package net.avicus.magma.api.graph.mutations.friend_remove.remove_response_data;

import com.shopify.graphql.support.Query;

public class RemoveResponseDataQuery extends Query<RemoveResponseDataQuery> {

  public RemoveResponseDataQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * If a friend request for this user was canceled.
   */
  public RemoveResponseDataQuery canceled() {
    startField("canceled");

    return this;
  }

  /**
   * If the user is not friends with the person they are attempting to remove.
   */
  public RemoveResponseDataQuery notFriends() {
    startField("not_friends");

    return this;
  }

  /**
   * If a the friend was removed.
   */
  public RemoveResponseDataQuery removed() {
    startField("removed");

    return this;
  }
}
