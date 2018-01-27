package net.avicus.magma.api.graph.types.report;

import com.shopify.graphql.support.Query;

public class ReportQuery extends Query<ReportQuery> {

  public ReportQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Date when this report was created.
   */
  public ReportQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * ID of the user who made the report.
   */
  public ReportQuery creatorId() {
    startField("creator_id");

    return this;
  }

  /**
   * The reason this report was made.
   */
  public ReportQuery reason() {
    startField("reason");

    return this;
  }

  /**
   * Name of the server which this report was made on.
   */
  public ReportQuery server() {
    startField("server");

    return this;
  }

  /**
   * ID of the user who is being reported.
   */
  public ReportQuery userId() {
    startField("user_id");

    return this;
  }
}
