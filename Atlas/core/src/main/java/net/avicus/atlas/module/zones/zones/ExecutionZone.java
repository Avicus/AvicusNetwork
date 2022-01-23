package net.avicus.atlas.module.zones.zones;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.event.world.BlockChangeByPlayerEvent;
import net.avicus.atlas.event.world.BlockChangeEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.registry.WeakReference;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.EntityVariable;
import net.avicus.atlas.module.checks.variable.MaterialVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.executors.Executor;
import net.avicus.atlas.module.zones.Zone;
import net.avicus.atlas.module.zones.ZoneMessage;
import net.avicus.magma.util.region.Region;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import tc.oc.tracker.event.PlayerCoarseMoveEvent;

@ToString(callSuper = true)
public class ExecutionZone extends Zone {

  protected final Optional<WeakReference<Executor>> enterExecutor;
  protected final Optional<WeakReference<Executor>> exitExecutor;
  protected final Optional<WeakReference<Executor>> breakExecutor;
  protected final Optional<WeakReference<Executor>> placeExecutor;
  protected final Optional<WeakReference<Executor>> useExecutor;
  protected final Optional<WeakReference<Executor>> modifyExecutor;

  public ExecutionZone(Match match,
      Region region,
      Optional<ZoneMessage> message,
      Optional<WeakReference<Executor>> enterExecutor,
      Optional<WeakReference<Executor>> exitExecutor,
      Optional<WeakReference<Executor>> breakExecutor,
      Optional<WeakReference<Executor>> placeExecutor,
      Optional<WeakReference<Executor>> useExecutor,
      Optional<WeakReference<Executor>> modifyExecutor) {
    super(match, region, message);
    this.enterExecutor = enterExecutor;
    this.exitExecutor = exitExecutor;
    this.breakExecutor = breakExecutor;
    this.placeExecutor = placeExecutor;
    this.useExecutor = useExecutor;
    this.modifyExecutor = modifyExecutor;
  }

  @Override
  public boolean isActive() {
    return this.enterExecutor.isPresent() ||
        this.exitExecutor.isPresent() ||
        this.breakExecutor.isPresent() ||
        this.placeExecutor.isPresent() ||
        this.useExecutor.isPresent() ||
        this.modifyExecutor.isPresent();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onTP(PlayerTeleportEvent event) {
    handleMove(event.getPlayer(), event.getFrom(), event.getTo());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onMove(PlayerCoarseMoveEvent event) {
    handleMove(event.getPlayer(), event.getFrom(), event.getTo());
  }

  public void handleMove(Player player, Location fromLoc, Location toLoc) {
    if (isObserving(this.match, player)) {
      return;
    }

    boolean from = getRegion().contains(fromLoc);

    boolean to = getRegion().contains(toLoc);

    if (from && !to) {
      handle(this.exitExecutor, new PlayerMoveEvent(player, fromLoc, toLoc));
    }

    if (!from && to) {
      handle(this.enterExecutor, new PlayerMoveEvent(player, fromLoc, toLoc));
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void blockChangeByPlayer(BlockChangeByPlayerEvent event) {
    this.onBlockChange(event);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockChange(BlockChangeEvent event) {
    if (!getRegion().contains(event.getBlock())) {
      return;
    }

    handle(this.modifyExecutor, event);

    if ((event.getCause() instanceof BlockBreakEvent || event
        .getCause() instanceof EntityExplodeEvent)) {
      handle(this.breakExecutor, event);
    }

    if (event.getCause() instanceof BlockPlaceEvent) {
      handle(this.placeExecutor, event);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBucketFill(PlayerBucketFillEvent event) {
    this.onBucket(event);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBucketEmpty(PlayerBucketEmptyEvent event) {
    this.onBucket(event);
  }

  private void onBucket(PlayerBucketEvent event) {
    if (!getRegion().contains(event.getBlockClicked())) {
      return;
    }

    handle(this.useExecutor, event);

    if (event instanceof PlayerBucketFillEvent) {
      handle(this.breakExecutor, event);
    }

    if (event instanceof PlayerBucketFillEvent) {
      handle(this.placeExecutor, event);
    }
  }

  public void handle(Optional<WeakReference<Executor>> toExecute, Event event) {
    CheckContext context = new CheckContext(this.match);

    if (event instanceof PlayerEvent) {
      context.add(new PlayerVariable(((PlayerEvent) event).getPlayer()));
    } else if (event instanceof BlockChangeByPlayerEvent) {
      context.add(new PlayerVariable(((BlockChangeByPlayerEvent) event).getPlayer()));
    } else if (event instanceof EntityEvent) {
      context.add(new EntityVariable(((EntityEvent) event).getEntity()));
    }

    if (event instanceof BlockChangeEvent) {
      context.add(new MaterialVariable(((BlockChangeEvent) event).getBlock().getState().getData()));
    }

    toExecute.ifPresent(w -> w.ifPresent(e -> e.executeChecked(context)));
  }

  @Override
  public String getDescription(CommandSender viewer) {
    return "Execution Zone" + super.getDescription(viewer);
  }
}
