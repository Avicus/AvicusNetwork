package net.avicus.atlas.module.fakeguis;

import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.states.StatesModule;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * This module is responsible for creating fake GUIs for blocks that are not containers.
 */
@ToString
public class FakeGUIsModule implements Module {

  private final boolean fakeBenches;
  private final boolean fakeEnchantTables;
  private final StatesModule statesModule;
  private final GroupsModule groupsModule;

  public FakeGUIsModule(Match match, boolean fakeBenches,
                        boolean fakeEnchantTables) {
    this.fakeBenches = fakeBenches;
    this.fakeEnchantTables = fakeEnchantTables;
    this.statesModule = match.getRequiredModule(StatesModule.class);
    this.groupsModule = match.getRequiredModule(GroupsModule.class);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onInteract(PlayerInteractEvent event) {
    final Player player = event.getPlayer();
    if (!groupsModule.getCompetitorOf(player).isPresent() ||
        event.getAction() != Action.RIGHT_CLICK_BLOCK ||
        !statesModule.isPlaying() ||
        player.isSneaking()) {
      return;
    }

    final Block block = event.getClickedBlock();
    if (block == null) {
      return;
    }

    final Material material = block.getType();

    if (material == Material.WORKBENCH && this.fakeBenches) {
      event.setCancelled(true);
      player.openWorkbench(null, true);
      return;
    }

    if (material == Material.ENCHANTMENT_TABLE && this.fakeEnchantTables) {
      event.setCancelled(true);
      player.openEnchanting(block.getLocation(), true);
      return;
    }
  }
}
