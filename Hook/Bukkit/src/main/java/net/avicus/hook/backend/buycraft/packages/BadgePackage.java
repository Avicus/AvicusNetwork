package net.avicus.hook.backend.buycraft.packages;

import java.util.Date;
import java.util.Map;
import net.avicus.hook.backend.buycraft.BuycraftPackage;
import net.avicus.hook.gadgets.types.badge.BadgeGadget;
import net.avicus.hook.gadgets.types.badge.BadgeSymbol;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.Gadget;
import org.bukkit.ChatColor;

public class BadgePackage implements BuycraftPackage {

  private final BadgeSymbol symbol;

  public BadgePackage(BadgeSymbol symbol) {
    this.symbol = symbol;
  }

  @Override
  public void execute(Status status, User user, Map<String, String> variables) {
    if (status != Status.INITIAL) {
      return;
    }

    String colorInput = variables.get("badge-color");

    ChatColor color = ChatColor.WHITE;

    try {
      color = ChatColor.valueOf(colorInput.toUpperCase());
    } catch (Exception e) {
      // ignore
    }

    Gadget gadget = new BadgeGadget(this.symbol, color);
    gadget.getManager().getGadgets()
        .createBackpackGadget(user, gadget.defaultContext(), false, new Date());
  }
}
