package net.avicus.magma.api.graph.inputs;

import com.shopify.graphql.support.Query;
import java.io.Serializable;

public class FriendAddInput implements Serializable {

  private int userId;

  private int friendId;

  private String clientMutationId;
  private boolean clientMutationIdSeen = false;

  public FriendAddInput(int userId, int friendId) {
    this.userId = userId;

    this.friendId = friendId;
  }

  public int getUserId() {
    return userId;
  }

  public FriendAddInput setUserId(int userId) {
    this.userId = userId;
    return this;
  }

  public int getFriendId() {
    return friendId;
  }

  public FriendAddInput setFriendId(int friendId) {
    this.friendId = friendId;
    return this;
  }

  public String getClientMutationId() {
    return clientMutationId;
  }

  public FriendAddInput setClientMutationId(String clientMutationId) {
    this.clientMutationId = clientMutationId;
    this.clientMutationIdSeen = true;
    return this;
  }

  // Unsets the clientMutationId property so that it is not serialized.
  public FriendAddInput unsetClientMutationId() {
    this.clientMutationId = null;
    this.clientMutationIdSeen = false;
    return this;
  }

  public void appendTo(StringBuilder builder) {
    String separator = "";
    builder.append('{');

    builder.append(separator);
    separator = ",";
    builder.append("user_id:");
    builder.append(userId);

    builder.append(separator);
    separator = ",";
    builder.append("friend_id:");
    builder.append(friendId);

    if (this.clientMutationIdSeen) {
      builder.append(separator);
      separator = ",";
      builder.append("clientMutationId:");
      if (clientMutationId != null) {
        Query.appendQuotedString(builder, clientMutationId.toString());
      } else {
        builder.append("null");
      }
    }

    builder.append('}');
  }
}
