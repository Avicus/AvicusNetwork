package net.avicus.atlas.module.zones.zones;

import java.util.Optional;
import java.util.Random;
import lombok.ToString;
import net.avicus.atlas.event.world.BlockChangeByPlayerEvent;
import net.avicus.atlas.event.world.BlockChangeEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.zones.Zone;
import net.avicus.atlas.module.zones.ZoneMessage;
import net.avicus.magma.util.region.Region;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.joda.time.Duration;
import tc.oc.tracker.Trackers;
import tc.oc.tracker.trackers.ExplosiveTracker;

@ToString(callSuper = true)
public class TNTCustomizationZone extends Zone {

  private final Random random = new Random();

  private final Float yield;
  private final Float power;
  private final boolean instantIgnite;
  private final Duration fuse;
  private final int dispenserNukeLimit;
  private final Float dispenserNukeMultiplier;

  public TNTCustomizationZone(Match match,
      Region region,
      Optional<ZoneMessage> message,
      Float yield,
      Float power,
      boolean instantIgnite,
      Duration fuse,
      int dispenserNukeLimit,
      Float dispenserNukeMultiplier) {
    super(match, region, message);
    this.yield = yield;
    this.power = power;
    this.instantIgnite = instantIgnite;
    this.fuse = fuse;
    this.dispenserNukeLimit = dispenserNukeLimit;
    this.dispenserNukeMultiplier = dispenserNukeMultiplier;
  }

  @Override
  public boolean isActive() {
    return this.yield != null ||
        this.power != null ||
        this.instantIgnite ||
        this.fuse != null ||
        this.dispenserNukeLimit > Integer.MIN_VALUE ||
        this.dispenserNukeMultiplier > Integer.MIN_VALUE;
  }

  public int getFuseTicks() {
    assert this.fuse != null;
    return (int) (this.fuse.getMillis() / 50.0);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void yieldSet(EntityExplodeEvent event) {
    if (event.getEntity() instanceof TNTPrimed) {
      if (this.yield != null) {
        event.setYield(this.yield);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void handleInstantActivation(BlockPlaceEvent event) {
    if (!getRegion().contains(event.getBlock())) {
      return;
    }

    if (this.instantIgnite && event.getBlock().getType() == Material.TNT) {
      World world = event.getBlock().getWorld();
      TNTPrimed tnt = world.spawn(event.getBlock().getLocation(), TNTPrimed.class);

      if (this.fuse != null) {
        tnt.setFuseTicks(this.getFuseTicks());
      }

      if (this.power != null) {
        tnt.setYield(this.power);
      }

      event.setCancelled(true);
      world.playSound(tnt.getLocation(), Sound.FUSE, 1, 1);

      ItemStack inHand = event.getItemInHand();
      if (inHand.getAmount() == 1) {
        inHand = null;
      } else {
        inHand.setAmount(inHand.getAmount() - 1);
      }
      event.getPlayer().getInventory()
          .setItem(event.getPlayer().getInventory().getHeldItemSlot(), inHand);
    }

  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void setCustomProperties(ExplosionPrimeEvent event) {
    if (!getRegion().contains(event.getEntity())) {
      return;
    }

    if (event.getEntity() instanceof TNTPrimed) {
      TNTPrimed tnt = (TNTPrimed) event.getEntity();

      if (this.fuse != null) {
        tnt.setFuseTicks(this.getFuseTicks());
      }

      if (this.power != null) {
        tnt.setYield(this.power);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void dispenserNukes(BlockChangeByPlayerEvent event) {
    handleDispense(event);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void handleDispense(BlockChangeEvent event) {
    if (!getRegion().contains(event.getBlock())) {
      return;
    }

    BlockState oldState = event.getOldState();
    if (oldState instanceof Dispenser &&
        this.dispenserNukeLimit > 0 &&
        this.dispenserNukeMultiplier > 0 &&
        event.getCause() instanceof EntityExplodeEvent) {

      Dispenser dispenser = (Dispenser) oldState;
      int tntLimit = Math.round(this.dispenserNukeLimit / this.dispenserNukeMultiplier);
      int tntCount = 0;

      ExplosiveTracker tracker = Trackers.getTracker(ExplosiveTracker.class);

      boolean trackPlayer = event instanceof BlockChangeByPlayerEvent;

      for (ItemStack stack : dispenser.getInventory().getContents()) {
        if (stack != null && stack.getType() == Material.TNT) {
          int transfer = Math.min(stack.getAmount(), tntLimit - tntCount);
          if (transfer > 0) {
            stack.setAmount(stack.getAmount() - transfer);
            tntCount += transfer;
          }
        }
      }

      tntCount = (int) Math.ceil(tntCount * this.dispenserNukeMultiplier);

      for (int i = 0; i < tntCount; i++) {
        TNTPrimed tnt = this.getMatch().getWorld().spawn(dispenser.getLocation(), TNTPrimed.class);

        tnt.setFuseTicks(10 + this.random
            .nextInt(10)); // between 0.5 and 1.0 seconds, same as vanilla TNT chaining

        Vector velocity = new Vector(random.nextGaussian(), random.nextGaussian(),
            random.nextGaussian()); // uniform random direction
        velocity.normalize().multiply(0.5 + 0.5 * random.nextDouble());
        tnt.setVelocity(velocity);

        if (trackPlayer) {
          tracker.setOwner(tnt, ((BlockChangeByPlayerEvent) event).getPlayer());
        }
      }
    }
  }
}
