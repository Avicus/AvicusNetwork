package net.avicus.magma.api.graph.types.team;

import com.shopify.graphql.support.Query;

public class TeamQuery extends Query<TeamQuery> {

  public TeamQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * The about section on the team page in raw HTML.
   */
  public TeamQuery about() {
    startField("about");

    return this;
  }

  /**
   * Date when this team was created.
   */
  public TeamQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * The tag of the team.
   */
  public TeamQuery tag() {
    startField("tag");

    return this;
  }

  /**
   * The tagline of the team.
   */
  public TeamQuery tagline() {
    startField("tagline");

    return this;
  }

  /**
   * The title of the team.
   */
  public TeamQuery title() {
    startField("title");

    return this;
  }

  /**
   * Date when this team was last updated.
   */
  public TeamQuery updatedAt() {
    startField("updated_at");

    return this;
  }
}
