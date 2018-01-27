package net.avicus.hook.credits;

import net.avicus.compendium.locale.text.Localizable;
import org.bukkit.entity.Player;

public interface GadgetPurchaseRequirement {

  Localizable getText();

  boolean meetsRequirement(Player player);
}
