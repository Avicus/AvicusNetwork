package net.avicus.magma.api.graph.inputs;

import com.shopify.graphql.support.Query;
import java.io.Serializable;

public class GadgetUpdateInput implements Serializable {

  private int userId;

  private int gadgetId;

  private String clientMutationId;
  private boolean clientMutationIdSeen = false;

  public GadgetUpdateInput(int userId, int gadgetId) {
    this.userId = userId;

    this.gadgetId = gadgetId;
  }

  public int getUserId() {
    return userId;
  }

  public GadgetUpdateInput setUserId(int userId) {
    this.userId = userId;
    return this;
  }

  public int getGadgetId() {
    return gadgetId;
  }

  public GadgetUpdateInput setGadgetId(int gadgetId) {
    this.gadgetId = gadgetId;
    return this;
  }

  public String getClientMutationId() {
    return clientMutationId;
  }

  public GadgetUpdateInput setClientMutationId(String clientMutationId) {
    this.clientMutationId = clientMutationId;
    this.clientMutationIdSeen = true;
    return this;
  }

  // Unsets the clientMutationId property so that it is not serialized.
  public GadgetUpdateInput unsetClientMutationId() {
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
    builder.append("gadget_id:");
    builder.append(gadgetId);

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
