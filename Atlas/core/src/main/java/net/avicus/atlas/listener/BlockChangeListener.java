package net.avicus.atlas.listener;

import java.util.Iterator;
import net.avicus.atlas.event.world.BlockChangeByPlayerEvent;
import net.avicus.atlas.event.world.BlockChangeEvent;
import net.avicus.atlas.util.Events;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import tc.oc.tracker.Trackers;
import tc.oc.tracker.trackers.ExplosiveTracker;

public class BlockChangeListener implements Listener {

  private BlockState toAirState(BlockState state) {
    return toMaterial(state, new MaterialData(Material.AIR, (byte) 0));
  }

  private BlockState toMaterial(BlockState state, MaterialData data) {
    BlockState blockState = new FakeBlockState(state);
    blockState.setType(data.getItemType());
    blockState.setData(data);
    return blockState;
  }

  private boolean callBlockChange(Block block, Event cause, BlockState from, BlockState to) {
    BlockChangeEvent call = new BlockChangeEvent<>(block, cause, from, to);
    Events.call(call);
    return call.isCancelled();
  }

  private boolean callBlockChange(Block block, Event cause, BlockState from, BlockState to,
      Player player) {
    BlockChangeByPlayerEvent call = new BlockChangeByPlayerEvent<>(block, cause, from, to, player);
    Events.call(call);
    return call.isCancelled();
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onBlockPistonExtend(BlockPistonExtendEvent event) {
    for (Block to : event.getBlocks()) {
      Block from = to.getRelative(event.getDirection());

      boolean cancel = callBlockChange(from, event, from.getState(), to.getState());
      if (cancel) {
        event.setCancelled(true);
        break;
      }
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onBlockPistonRetract(BlockPistonRetractEvent event) {
    for (Block from : event.getBlocks()) {
      Block to = from.getRelative(event.getDirection());

      boolean cancel = callBlockChange(to, event, from.getState(), to.getState());
      if (cancel) {
        event.setCancelled(true);
        break;
      }
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onBlockBreak(BlockBreakEvent event) {
    BlockState from = event.getBlock().getState();
    BlockState to = toAirState(event.getBlock().getState());

    boolean cancel = callBlockChange(event.getBlock(), event, from, to, event.getPlayer());
    event.setCancelled(cancel);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onBlockPlace(BlockPlaceEvent event) {
    BlockState from = event.getBlock().getState();
    BlockState to = event.getBlockPlaced().getState();

    boolean cancel = callBlockChange(event.getBlock(), event, from, to, event.getPlayer());
    event.setCancelled(cancel);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onFromTo(BlockFromToEvent event) {
    // Canceled water flow events can sometimes spam this
    if (event.getToBlock().getType() != event.getBlock().getType()) {
      BlockState oldState = event.getToBlock().getState();
      BlockState newState = event.getToBlock().getState();
      newState.setType(event.getBlock().getType());
      newState.setRawData(event.getBlock().getData());

      callBlockChange(event.getToBlock(), event, oldState, newState);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onBlockGrow(BlockGrowEvent event) {
    boolean cancel = callBlockChange(event.getBlock(), event, event.getBlock().getState(),
        event.getNewState());
    event.setCancelled(cancel);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onBlockFade(BlockFadeEvent event) {
    boolean cancel = callBlockChange(event.getBlock(), event, event.getBlock().getState(),
        event.getNewState());
    event.setCancelled(cancel);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onBlockForm(BlockFormEvent event) {
    boolean cancel = callBlockChange(event.getBlock(), event, event.getBlock().getState(),
        event.getNewState());
    event.setCancelled(cancel);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onBlockIgnite(BlockIgniteEvent event) {
    // from TNT to TNT entity (aka AIR)
    boolean cancel = callBlockChange(event.getBlock(), event, event.getBlock().getState(),
        toAirState(event.getBlock().getState()));
    event.setCancelled(cancel);
  }


  @EventHandler(priority = EventPriority.LOW)
  public void onEntityExplode(EntityExplodeEvent event) {
    Player owner = null;
    if (event.getEntity() instanceof TNTPrimed) {
      owner = Trackers.getTracker(ExplosiveTracker.class)
          .getOwner((TNTPrimed) event.getEntity());
    }

    Iterator<Block> iterator = event.blockList().iterator();
    while (iterator.hasNext()) {
      Block block = iterator.next();
      BlockState air = toAirState(block.getState());

      boolean cancel;
      if (owner == null) {
        cancel = callBlockChange(block, event, block.getState(), air);
      } else {
        cancel = callBlockChange(block, event, block.getState(), air, owner);
      }

      if (cancel) {
        iterator.remove();
      }
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onBlockSpread(BlockSpreadEvent event) {
    boolean cancel = callBlockChange(event.getBlock(), event, event.getBlock().getState(),
        event.getNewState());
    event.setCancelled(cancel);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onBlockBurn(BlockBurnEvent event) {
    boolean cancel = callBlockChange(event.getBlock(), event, event.getBlock().getState(),
        toAirState(event.getBlock().getState()));
    event.setCancelled(cancel);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onBlockDispense(BlockDispenseEvent event) {
    if (!event.getItem().getType().equals(Material.BUCKET)) {
      return;
    }

    Block block = event.getBlock();

    if (!block.isLiquid()) {
      return;
    }

    boolean cancel = callBlockChange(block, event, block.getState(), toAirState(block.getState()));
    event.setCancelled(cancel);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onEntityChangeBlock(EntityChangeBlockEvent event) {
    BlockState oldState = event.getBlock().getState();
    BlockState newState = toMaterial(oldState, new MaterialData(event.getTo(), event.getData()));
    boolean cancel = callBlockChange(event.getBlock(), event, oldState, newState);
    event.setCancelled(cancel);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.getAction() != Action.PHYSICAL) {
      return;
    }

    // only save the farmers
    if (event.getClickedBlock().getType() != Material.SOIL) {
      return;
    }

    BlockState oldState = event.getClickedBlock().getState();
    BlockState newState = toMaterial(oldState, new MaterialData(Material.DIRT, (byte) 0));

    boolean cancel = callBlockChange(oldState.getBlock(), event, oldState, newState,
        event.getPlayer());
    event.setCancelled(cancel);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerEmptyBucket(PlayerBucketEmptyEvent event) {
    Block block = event.getBlockClicked().getRelative(event.getBlockFace());

    Material material;
    if (event.getBucket() == Material.LAVA_BUCKET) {
      material = Material.LAVA;
    } else if (event.getBucket() == Material.WATER_BUCKET) {
      material = Material.WATER;
    } else {
      return;
    }

    BlockState oldState = block.getState();
    BlockState newState = toMaterial(oldState, new MaterialData(material, (byte) 0));

    boolean cancel = callBlockChange(block, event, oldState, newState);
    event.setCancelled(cancel);
  }

  // todo: other events such as water flow, block grow, explosions...
}
