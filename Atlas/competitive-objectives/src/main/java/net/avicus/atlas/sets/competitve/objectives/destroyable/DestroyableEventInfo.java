package net.avicus.atlas.sets.competitve.objectives.destroyable;

import lombok.Data;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Data
public class DestroyableEventInfo {

  private final Player actor;
  private final ItemStack tool;
  private final Material broken;
  private final boolean byHand;
}
