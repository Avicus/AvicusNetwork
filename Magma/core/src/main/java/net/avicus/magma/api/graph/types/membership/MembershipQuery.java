package net.avicus.magma.api.graph.types.membership;

import com.shopify.graphql.support.Query;

public class MembershipQuery extends Query<MembershipQuery> {

  public MembershipQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Date when this membership was created.
   */
  public MembershipQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * When this membership expires.
   */
  public MembershipQuery expiresAt() {
    startField("expires_at");

    return this;
  }

  /**
   * If this rank was purchased from the store.
   */
  public MembershipQuery isPurchased() {
    startField("is_purchased");

    return this;
  }

  /**
   * ID of the user who this membership is for.
   */
  public MembershipQuery memberId() {
    startField("member_id");

    return this;
  }

  /**
   * ID of the rank which the user belongs to.
   */
  public MembershipQuery rankId() {
    startField("rank_id");

    return this;
  }

  /**
   * Date when this membership was last updated.
   */
  public MembershipQuery updatedAt() {
    startField("updated_at");

    return this;
  }
}
