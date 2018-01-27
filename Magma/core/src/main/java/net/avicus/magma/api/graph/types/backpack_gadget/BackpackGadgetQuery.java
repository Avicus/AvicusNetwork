package net.avicus.magma.api.graph.types.backpack_gadget;

import com.shopify.graphql.support.Query;

public class BackpackGadgetQuery extends Query<BackpackGadgetQuery> {

  public BackpackGadgetQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Context of the gadget related to the specific user.
   */
  public BackpackGadgetQuery context() {
    startField("context");

    return this;
  }

  /**
   * Date when this backpackgadget was created.
   */
  public BackpackGadgetQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * Special data associated with the gadget regardless of context.
   */
  public BackpackGadgetQuery gadget() {
    startField("gadget");

    return this;
  }

  /**
   * Type of the base gadget of this item.
   */
  public BackpackGadgetQuery gadgetType() {
    startField("gadget_type");

    return this;
  }

  /**
   * ID of the gadget before the Atlas conversion.
   */
  public BackpackGadgetQuery oldId() {
    startField("old_id");

    return this;
  }

  /**
   * Date when this backpackgadget was last updated.
   */
  public BackpackGadgetQuery updatedAt() {
    startField("updated_at");

    return this;
  }

  /**
   * ID of the user who has this gadget in their backpack.
   */
  public BackpackGadgetQuery userId() {
    startField("user_id");

    return this;
  }
}
