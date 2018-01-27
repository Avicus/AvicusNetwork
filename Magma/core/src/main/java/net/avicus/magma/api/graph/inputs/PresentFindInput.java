package net.avicus.magma.api.graph.inputs;

import com.shopify.graphql.support.Query;
import java.io.Serializable;

public class PresentFindInput implements Serializable {

  private int userId;

  private String slug;

  private String family;

  private String clientMutationId;
  private boolean clientMutationIdSeen = false;

  private Boolean create;
  private boolean createSeen = false;

  public PresentFindInput(int userId, String slug, String family) {
    this.userId = userId;

    this.slug = slug;

    this.family = family;
  }

  public int getUserId() {
    return userId;
  }

  public PresentFindInput setUserId(int userId) {
    this.userId = userId;
    return this;
  }

  public String getSlug() {
    return slug;
  }

  public PresentFindInput setSlug(String slug) {
    this.slug = slug;
    return this;
  }

  public String getFamily() {
    return family;
  }

  public PresentFindInput setFamily(String family) {
    this.family = family;
    return this;
  }

  public String getClientMutationId() {
    return clientMutationId;
  }

  public PresentFindInput setClientMutationId(String clientMutationId) {
    this.clientMutationId = clientMutationId;
    this.clientMutationIdSeen = true;
    return this;
  }

  // Unsets the clientMutationId property so that it is not serialized.
  public PresentFindInput unsetClientMutationId() {
    this.clientMutationId = null;
    this.clientMutationIdSeen = false;
    return this;
  }

  public Boolean getCreate() {
    return create;
  }

  public PresentFindInput setCreate(Boolean create) {
    this.create = create;
    this.createSeen = true;
    return this;
  }

  // Unsets the create property so that it is not serialized.
  public PresentFindInput unsetCreate() {
    this.create = null;
    this.createSeen = false;
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
    builder.append("slug:");
    Query.appendQuotedString(builder, slug.toString());

    builder.append(separator);
    separator = ",";
    builder.append("family:");
    Query.appendQuotedString(builder, family.toString());

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

    if (this.createSeen) {
      builder.append(separator);
      separator = ",";
      builder.append("create:");
      if (create != null) {
        builder.append(create);
      } else {
        builder.append("null");
      }
    }

    builder.append('}');
  }
}
