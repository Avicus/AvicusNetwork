package net.avicus.magma.module.gadgets;

import com.google.gson.JsonObject;

/**
 * A gadget context that contains no data.
 */
public class EmptyGadgetContext<G extends Gadget> extends AbstractGadgetContext<G> {

  public EmptyGadgetContext(G gadget) {
    super(gadget);
  }

  @Override
  public JsonObject serialize() {
    return new JsonObject();
  }
}
