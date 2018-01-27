package net.avicus.magma.api.graph.types.experience_transaction;

import com.shopify.graphql.support.Query;

public class ExperienceTransactionQuery extends Query<ExperienceTransactionQuery> {

  public ExperienceTransactionQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Number of XP this transaction should represent.
   */
  public ExperienceTransactionQuery amount() {
    startField("amount");

    return this;
  }

  /**
   * Date when this experiencetransaction was created.
   */
  public ExperienceTransactionQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * Game genre of this transaction.
   */
  public ExperienceTransactionQuery genre() {
    startField("genre");

    return this;
  }

  /**
   * ID of the season which this transaction happened inside of
   */
  public ExperienceTransactionQuery seasonId() {
    startField("season_id");

    return this;
  }

  /**
   * ID of the user that the XP in this transaction is rewarded to.
   */
  public ExperienceTransactionQuery userId() {
    startField("user_id");

    return this;
  }

  /**
   * The base XP value was multiplied by. The amount represented by this object already reflects
   * this operation.
   */
  public ExperienceTransactionQuery weight() {
    startField("weight");

    return this;
  }
}
