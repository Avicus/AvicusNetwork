package net.avicus.magma.module.gadgets;

public abstract class AbstractGadget<C extends GadgetContext> implements Gadget<C> {

  private final GadgetManager manager;

  public AbstractGadget(GadgetManager manager) {
    this.manager = manager;
  }

  @Override
  public GadgetManager getManager() {
    return this.manager;
  }
}
