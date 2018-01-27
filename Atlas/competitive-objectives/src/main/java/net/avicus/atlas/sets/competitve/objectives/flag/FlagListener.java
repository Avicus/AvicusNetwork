package net.avicus.atlas.sets.competitve.objectives.flag;

import java.util.List;
import java.util.Optional;
import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import net.avicus.atlas.event.match.MatchCloseEvent;
import net.avicus.atlas.event.match.MatchStateChangeEvent;
import net.avicus.atlas.event.world.BlockChangeByPlayerEvent;
import net.avicus.atlas.event.world.BlockChangeEvent;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.teams.TeamsModule;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.zones.Zone;
import net.avicus.atlas.module.zones.ZonesModule;
import net.avicus.atlas.sets.competitve.objectives.zones.flag.NetZone;
import net.avicus.atlas.sets.competitve.objectives.zones.flag.PostZone;
import net.avicus.atlas.util.Messages;
import net.avicus.grave.event.PlayerDeathEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.util.Vector;
import org.joda.time.DateTime;
import tc.oc.tracker.event.PlayerCoarseMoveEvent;

public class FlagListener implements Listener {

  private final ObjectivesModule module;
  private final List<FlagObjective> flags;
  private final List<PostZone> posts;
  private final List<NetZone> nets;
  private final FlagHighlightTask highlightTask;

  public FlagListener(ObjectivesModule module, List<FlagObjective> flags) {
    this.module = module;
    this.flags = flags;
    this.posts = module.getMatch().getModule(ZonesModule.class).get().getZones(PostZone.class);
    this.nets = module.getMatch().getModule(ZonesModule.class).get().getZones(NetZone.class);
    this.highlightTask = new FlagHighlightTask(module, flags);
  }

  @EventHandler
  public void onStateChange(MatchStateChangeEvent event) {
    if (event.isChangeToPlaying()) {
      this.highlightTask.start();
    }

    if (event.isChangeToNotPlaying()) {
      this.highlightTask.cancel0();

      for (FlagObjective flag : this.flags) {
        if (flag.isCarried() && flag.getCurrentLocation().isPresent()) {
          flag.placeFlag(flag.getCurrentLocation().get().toVector(), 0, false);
        }
        flag.getFlagCountdown().ifPresent(flag.getCm()::cancel);
      }
    }
  }

  @EventHandler
  public void onMatchClose(MatchCloseEvent event) {
    this.highlightTask.cancel0();
  }

  private boolean isBanner(Block block) {
    return block.getType() == Material.STANDING_BANNER || block.getType() == Material.WALL_BANNER;
  }

  @EventHandler
  public void onZoneEnter(PlayerCoarseMoveEvent event) {
    NetZone zone = null;

    for (NetZone test : this.nets) {
      if (test.getRegion().contains(event.getFrom())) {
        continue;
      }

      if (test.getRegion().contains(event.getTo())) {
        zone = test;
        break;
      }
    }

    if (zone == null) {
      return;
    }

    for (FlagObjective flag : this.flags) {
      if (!flag.isCarrier(event.getPlayer())) {
        continue;
      }

      if (!zone.canCapture(event.getPlayer(), flag)) {
        // todo: message "can't capture rn"?
        continue;
      }

      zone.capture(event.getPlayer(), flag);
      zone.queueRespawn(flag);
      break;
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (!(event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction()
        .equals(Action.RIGHT_CLICK_BLOCK))) {
      return;
    }

    Block block = event.getClickedBlock();
    if (!isBanner(block)) {
      return;
    }

    Player player = event.getPlayer();

    if (this.module.getMatch().getRequiredModule(GroupsModule.class).isObserving(player)) {
      return;
    }

    for (FlagObjective flag : this.flags) {
      if (!flag.getCurrentPlacement().equals(Optional.of(block))) {
        continue;
      }
      if (flag.canPickup(player, FlagPickupMethod.INTERACT)) {
        flag.setCarrier(player);
        flag.setCurrentPost(Optional.empty());
        flag.setPickupDate(Optional.of(new DateTime()));
        event.setCancelled(true);
        break;
      } else {
        player.sendMessage(Messages.ERROR_OBJECTIVE_BREAK_POST.with(ChatColor.RED));
        event.setCancelled(true);
        break;
      }
    }
  }


  @EventHandler
  public void onCoarseMove(PlayerCoarseMoveEvent event) {
    if (!isBanner(event.getTo().getBlock())) {
      return;
    }

    Player player = event.getPlayer();

    if (this.module.getMatch().getRequiredModule(TeamsModule.class).isObservingOrDead(player)) {
      return;
    }

    for (FlagObjective flag : this.flags) {
      if (!flag.getCurrentPlacement().equals(Optional.of(event.getTo().getBlock()))) {
        continue;
      }

      if (!flag.canPickup(player, FlagPickupMethod.MOVE)) {
        continue;
      }

      flag.setCarrier(player);
      flag.setCurrentPost(Optional.empty());
      flag.setPickupDate(Optional.of(new DateTime()));
      break;
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void blockChangeByPlayer(BlockChangeByPlayerEvent event) {
    this.onBlockChange(event);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockChange(BlockChangeEvent event) {
    for (Zone post : this.posts) {
      if (post.getRegion().contains(event.getBlock())) {
        if (event instanceof BlockChangeByPlayerEvent) {
          Player player = ((BlockChangeByPlayerEvent) event).getPlayer();
          player.sendMessage(Messages.ERROR_OBJECTIVE_BREAK_POST.with(ChatColor.RED));
        }

        event.setCancelled(true);
        return;
      }
    }

    if (!(event instanceof BlockChangeByPlayerEvent)) {
      return;
    }

    Player player = ((BlockChangeByPlayerEvent) event).getPlayer();

    if (event.getNewState() instanceof Banner) {
      this.flags.stream()
          .filter(flag -> flag.isCarrier(player))
          .forEach(flag -> {
            Vector point = event.getBlock()
                .getLocation()
                .toVector();
            flag.setCurrentPost(Optional.empty());
            flag.placeFlag(point, 0, false);
          });
    }
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    for (FlagObjective flag : this.flags) {
      if (flag.isCarrier(event.getPlayer())) {
        for (int i = 0; i < event.getDrops().size(); i++) {
          ItemStack drop = event.getDrops().get(i);
          if (drop.getItemMeta() instanceof BannerMeta) {
            event.getDrops().remove(i);
            i--;
          }
        }

        if (flag.getCarrierHelmet().isPresent()) {
          event.getDrops().add(flag.getCarrierHelmet().get());
        }

        Vector point = event.getLocation().toVector();
        float yaw = event.getPlayer().getLocation().getYaw();

        flag.setCurrentPost(Optional.empty());
        flag.placeFlag(point, yaw, true);
      }
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    for (FlagObjective flag : this.flags) {
      if (!flag.isCarrier(event.getPlayer())) {
        continue;
      }

      Vector point = event.getPlayer().getLocation().toVector();
      float yaw = event.getPlayer().getLocation().getYaw();
      flag.setCurrentPost(Optional.empty());
      flag.placeFlag(point, yaw, true);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerChangeTeam(PlayerChangedGroupEvent event) {
    for (FlagObjective flag : this.flags) {
      if (!flag.isCarrier(event.getPlayer())) {
        continue;
      }

      Vector point = event.getPlayer().getLocation().toVector();
      float yaw = event.getPlayer().getLocation().getYaw();
      flag.setCurrentPost(Optional.empty());
      flag.placeFlag(point, yaw, true);
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    Player player = (Player) event.getWhoClicked();

    if (event.getCurrentItem() == null || event.getCurrentItem().getType() != Material.BANNER) {
      return;
    }

    for (FlagObjective flag : this.flags) {
      if (flag.isCarrier(player)) {
        event.setCancelled(true);
        break;
      }
    }
  }
}
