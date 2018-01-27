package net.avicus.magma.api.graph.types.present;

import com.shopify.graphql.support.Query;

public class PresentQuery extends Query<PresentQuery> {

  public PresentQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Family of the present e.g "Christmas 2017"
   */
  public PresentQuery family() {
    startField("family");

    return this;
  }

  /**
   * Description of the location of the present used in the UI.
   */
  public PresentQuery humanLocation() {
    startField("human_location");

    return this;
  }

  /**
   * Name of the present used in the UI.
   */
  public PresentQuery humanName() {
    startField("human_name");

    return this;
  }

  /**
   * Slug of the present used in plugins to protect against name changes.
   */
  public PresentQuery slug() {
    startField("slug");

    return this;
  }
}
