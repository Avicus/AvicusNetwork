package net.avicus.magma.api.graph.inputs;

import com.shopify.graphql.support.Query;
import java.io.Serializable;

public class AlertDeleteInput implements Serializable {

  private int alertId;

  private String clientMutationId;
  private boolean clientMutationIdSeen = false;

  public AlertDeleteInput(int alertId) {
    this.alertId = alertId;
  }

  public int getAlertId() {
    return alertId;
  }

  public AlertDeleteInput setAlertId(int alertId) {
    this.alertId = alertId;
    return this;
  }

  public String getClientMutationId() {
    return clientMutationId;
  }

  public AlertDeleteInput setClientMutationId(String clientMutationId) {
    this.clientMutationId = clientMutationId;
    this.clientMutationIdSeen = true;
    return this;
  }

  // Unsets the clientMutationId property so that it is not serialized.
  public AlertDeleteInput unsetClientMutationId() {
    this.clientMutationId = null;
    this.clientMutationIdSeen = false;
    return this;
  }

  public void appendTo(StringBuilder builder) {
    String separator = "";
    builder.append('{');

    builder.append(separator);
    separator = ",";
    builder.append("alert_id:");
    builder.append(alertId);

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
