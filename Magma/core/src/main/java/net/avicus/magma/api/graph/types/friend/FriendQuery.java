package net.avicus.magma.api.graph.types.friend;

import com.shopify.graphql.support.Query;

public class FriendQuery extends Query<FriendQuery> {

  public FriendQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * If the request has been accepted.
   */
  public FriendQuery accepted() {
    startField("accepted");

    return this;
  }

  /**
   * Date when this friend was created.
   */
  public FriendQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * ID of the user who was requested.
   */
  public FriendQuery friendId() {
    startField("friend_id");

    return this;
  }

  /**
   * ID of the user who initiated the friendship.
   */
  public FriendQuery userId() {
    startField("user_id");

    return this;
  }
}
