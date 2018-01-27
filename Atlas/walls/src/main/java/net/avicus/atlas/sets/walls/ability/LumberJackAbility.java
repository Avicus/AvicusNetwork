package net.avicus.atlas.sets.walls.ability;

import java.util.Optional;
import java.util.Random;
import net.avicus.atlas.event.world.BlockChangeByPlayerEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.checks.variable.MaterialVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.kits.KitAbility;
import net.avicus.atlas.util.AtlasTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class LumberJackAbility extends KitAbility {

  private static Random RANDOM = new Random();
  private final Match match;
  private final Optional<Check> check;

  public LumberJackAbility(Match match, Optional<Check> check) {
    super(match);
    this.match = match;
    this.check = check;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockBreak(BlockChangeByPlayerEvent event) {
    if (event.getCause() instanceof BlockBreakEvent &&
        (event.getBlock().getType() == Material.LOG || event.getBlock().getType() == Material.LOG_2)
        && hasAbility(event.getPlayer(), true)) {

      MaterialData data = event.getBlock().getState().getData();

      if (this.check.isPresent()) {
        CheckContext context = new CheckContext(this.match);
        context.add(new PlayerVariable(event.getPlayer()));
        context.add(new MaterialVariable(data));
        context.add(new LocationVariable(event.getBlock().getLocation()));

        if (this.check.get().test(context).fails()) {
          return;
        }
      }

      Location location = event.getBlock().getLocation();
      World world = location.getWorld();

      int delay = 0;
      for (int i = 1; i < 60; i++) {
        Location offset = location.clone().add(0, i, 0);
        Block block = world.getBlockAt(offset);
        if (!block.getState().getData().equals(data)) {
          break;
        }
        delay = delay + 5;
        AtlasTask.of(() -> {
          ItemStack stack = block.getState().getData().toItemStack(1);
          block.setType(Material.AIR);
          world.dropItem(offset, stack);
          world.playSound(block.getLocation(), Sound.STEP_WOOD, 1, 0.5f + RANDOM.nextFloat());
        }).later(delay);
      }
    }
  }
}
