package net.avicus.magma.api.graph.types.username;

import com.shopify.graphql.support.Query;

public class UsernameQuery extends Query<UsernameQuery> {

  public UsernameQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Date when this username was created.
   */
  public UsernameQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * ID of the user who had this username.
   */
  public UsernameQuery userId() {
    startField("user_id");

    return this;
  }

  /**
   * The username that the userhad.
   */
  public UsernameQuery username() {
    startField("username");

    return this;
  }
}
