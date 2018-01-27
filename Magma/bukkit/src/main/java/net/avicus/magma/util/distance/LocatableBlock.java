package net.avicus.magma.util.distance;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.function.Function;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class LocatableBlock extends LocatableObject<SimplePlayerStore, Player> implements Listener {

  private final Block block;

  public LocatableBlock(Block block) {
    super(Sets.newHashSet(
        new DistanceCalculationMetric(DistanceCalculationMetric.Type.BLOCK, false)));
    this.block = block;
  }

  @Override
  public Iterable<Vector> getDistanceReferenceLocations(Player base) {
    return Collections.singleton(block.getLocation().toVector());
  }

  @Override
  protected boolean canUpdateDistance(Player base) {
    return true;
  }

  @Override
  public Function<Player, SimplePlayerStore> conversionFunc() {
    return SimplePlayerStore::getFromPlayer;
  }

  @Override
  public boolean canViewAlways(Player base) {
    return true;
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onMove(PlayerMoveEvent event) {
    updateDistance(event.getPlayer(), event.getTo());
  }
}
