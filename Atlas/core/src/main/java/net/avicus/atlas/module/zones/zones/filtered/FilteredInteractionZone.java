package net.avicus.atlas.module.zones.zones.filtered;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.event.world.BlockChangeByPlayerEvent;
import net.avicus.atlas.event.world.BlockChangeEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.EntityVariable;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.checks.variable.MaterialVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.zones.Zone;
import net.avicus.atlas.module.zones.ZoneMessage;
import net.avicus.magma.util.region.Region;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

@ToString(callSuper = true)
public class FilteredInteractionZone extends Zone {

  private final Optional<Check> modify;
  private final Optional<Check> blockPlace;
  private final Optional<Check> blockBreak;
  private final Optional<Check> use;

  public FilteredInteractionZone(Match match, Region region, Optional<ZoneMessage> message,
      Optional<Check> modify, Optional<Check> blockPlace, Optional<Check> blockBreak,
      Optional<Check> use) {
    super(match, region, message);
    this.modify = modify;
    this.blockPlace = blockPlace;
    this.blockBreak = blockBreak;
    this.use = use;
  }

  @Override
  public boolean isActive() {
    return this.modify.isPresent() ||
        this.blockPlace.isPresent() ||
        this.blockBreak.isPresent() ||
        this.use.isPresent();
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void blockChangeByPlayer(BlockChangeByPlayerEvent event) {
    this.onBlockChange(event);
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onBlockChange(BlockChangeEvent event) {
    if (!getRegion().contains(event.getBlock())) {
      return;
    }

    if (this.modify.isPresent()) {
      if (!test(event, this.modify.get())) {
        if (event instanceof BlockChangeByPlayerEvent) {
          message(((BlockChangeByPlayerEvent) event).getPlayer());
        }
        event.setCancelled(true);
      }
    }

    if (this.blockBreak.isPresent() && (event.getCause() instanceof BlockBreakEvent || event
        .getCause() instanceof EntityExplodeEvent)) {
      if (!test(event, this.blockBreak.get())) {
        if (event instanceof BlockChangeByPlayerEvent) {
          message(((BlockChangeByPlayerEvent) event).getPlayer());
        }
        event.setCancelled(true);
      }
    }

    if (this.blockPlace.isPresent() && event.getCause() instanceof BlockPlaceEvent) {
      if (!test(event, this.blockPlace.get())) {
        if (event instanceof BlockChangeByPlayerEvent) {
          message(((BlockChangeByPlayerEvent) event).getPlayer());
        }
        event.setCancelled(true);
      }
    }

  }

  @EventHandler
  public void onBucketFill(PlayerBucketFillEvent event) {
    this.onBucket(event);
  }

  @EventHandler
  public void onBucketEmpty(PlayerBucketEmptyEvent event) {
    this.onBucket(event);
  }

  private void onBucket(PlayerBucketEvent event) {
    if (!getRegion().contains(event.getBlockClicked())) {
      return;
    }

    if (this.use.isPresent() && !test(event, this.use.get())) {
      message(event.getPlayer());
      event.setCancelled(true);
    }

    if (this.blockBreak.isPresent() && event instanceof PlayerBucketFillEvent) {
      if (!test(event, this.blockBreak.get())) {
        message(event.getPlayer());
        event.setCancelled(true);
      }
    }

    if (this.blockPlace.isPresent() && event instanceof PlayerBucketEmptyEvent) {
      if (!test(event, this.blockPlace.get())) {
        message(event.getPlayer());
        event.setCancelled(true);
      }
    }

  }

  private boolean test(BlockChangeEvent event, Check check) {
    CheckContext context = new CheckContext(this.match);
    context.add(new MaterialVariable(event.getBlock().getState().getData()));
    context.add(new LocationVariable(event.getBlock().getLocation()));
    if (event instanceof BlockChangeByPlayerEvent) {
      context.add(new PlayerVariable(((BlockChangeByPlayerEvent) event).getPlayer()));
    }
    if (event.getCause() instanceof EntityExplodeEvent) {
      context.add(new EntityVariable(((EntityExplodeEvent) event.getCause()).getEntity()));
    }

    return check.test(context).passes();
  }

  private boolean test(PlayerBucketEvent event, Check check) {
    CheckContext context = new CheckContext(this.match);
    context.add(new LocationVariable(event.getBlockClicked().getLocation()));
    context.add(new PlayerVariable(event.getPlayer()));
    return check.test(context).passes();
  }
}
