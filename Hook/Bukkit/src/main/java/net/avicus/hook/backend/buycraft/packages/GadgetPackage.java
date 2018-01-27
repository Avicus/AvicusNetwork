package net.avicus.hook.backend.buycraft.packages;

import java.util.Date;
import java.util.Map;
import net.avicus.hook.backend.buycraft.BuycraftPackage;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.Gadget;

public class GadgetPackage implements BuycraftPackage {

  private final Gadget gadget;
  private final int count;

  public GadgetPackage(Gadget gadget, int count) {
    this.gadget = gadget;
    this.count = count;
  }

  @Override
  public void execute(Status status, User user, Map<String, String> variables) {
    if (status != Status.INITIAL) {
      return;
    }

    for (int i = 0; i < this.count; i++) {
      gadget.getManager().getGadgets()
          .createBackpackGadget(user, this.gadget.defaultContext(), false, new Date());
    }
  }
}
