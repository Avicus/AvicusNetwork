package net.avicus.magma.api.graph.inputs;

import com.shopify.graphql.support.Query;
import java.io.Serializable;

public class AlertSendInput implements Serializable {

  private int id;

  private String name;

  private String url;

  private String text;

  private String clientMutationId;
  private boolean clientMutationIdSeen = false;

  public AlertSendInput(int id, String name, String url, String text) {
    this.id = id;

    this.name = name;

    this.url = url;

    this.text = text;
  }

  public int getId() {
    return id;
  }

  public AlertSendInput setId(int id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public AlertSendInput setName(String name) {
    this.name = name;
    return this;
  }

  public String getUrl() {
    return url;
  }

  public AlertSendInput setUrl(String url) {
    this.url = url;
    return this;
  }

  public String getText() {
    return text;
  }

  public AlertSendInput setText(String text) {
    this.text = text;
    return this;
  }

  public String getClientMutationId() {
    return clientMutationId;
  }

  public AlertSendInput setClientMutationId(String clientMutationId) {
    this.clientMutationId = clientMutationId;
    this.clientMutationIdSeen = true;
    return this;
  }

  // Unsets the clientMutationId property so that it is not serialized.
  public AlertSendInput unsetClientMutationId() {
    this.clientMutationId = null;
    this.clientMutationIdSeen = false;
    return this;
  }

  public void appendTo(StringBuilder builder) {
    String separator = "";
    builder.append('{');

    builder.append(separator);
    separator = ",";
    builder.append("id:");
    builder.append(id);

    builder.append(separator);
    separator = ",";
    builder.append("name:");
    Query.appendQuotedString(builder, name.toString());

    builder.append(separator);
    separator = ",";
    builder.append("url:");
    Query.appendQuotedString(builder, url.toString());

    builder.append(separator);
    separator = ",";
    builder.append("text:");
    Query.appendQuotedString(builder, text.toString());

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
