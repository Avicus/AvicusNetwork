package net.avicus.magma.api.graph.inputs;

import com.shopify.graphql.support.Query;
import java.io.Serializable;
import org.joda.time.DateTime;

public class UserLoginInput implements Serializable {

  private String username;

  private int server;

  private String uuid;

  private DateTime time;

  private String clientMutationId;
  private boolean clientMutationIdSeen = false;

  public UserLoginInput(String username, int server, String uuid, DateTime time) {
    this.username = username;

    this.server = server;

    this.uuid = uuid;

    this.time = time;
  }

  public String getUsername() {
    return username;
  }

  public UserLoginInput setUsername(String username) {
    this.username = username;
    return this;
  }

  public int getServer() {
    return server;
  }

  public UserLoginInput setServer(int server) {
    this.server = server;
    return this;
  }

  public String getUuid() {
    return uuid;
  }

  public UserLoginInput setUuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  public DateTime getTime() {
    return time;
  }

  public UserLoginInput setTime(DateTime time) {
    this.time = time;
    return this;
  }

  public String getClientMutationId() {
    return clientMutationId;
  }

  public UserLoginInput setClientMutationId(String clientMutationId) {
    this.clientMutationId = clientMutationId;
    this.clientMutationIdSeen = true;
    return this;
  }

  // Unsets the clientMutationId property so that it is not serialized.
  public UserLoginInput unsetClientMutationId() {
    this.clientMutationId = null;
    this.clientMutationIdSeen = false;
    return this;
  }

  public void appendTo(StringBuilder builder) {
    String separator = "";
    builder.append('{');

    builder.append(separator);
    separator = ",";
    builder.append("username:");
    Query.appendQuotedString(builder, username.toString());

    builder.append(separator);
    separator = ",";
    builder.append("server:");
    builder.append(server);

    builder.append(separator);
    separator = ",";
    builder.append("uuid:");
    Query.appendQuotedString(builder, uuid.toString());

    builder.append(separator);
    separator = ",";
    builder.append("time:");
    Query.appendQuotedString(builder, time.toString());

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
