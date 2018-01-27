package net.avicus.atlas.sets.competitve.objectives.destroyable.monument;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.avicus.atlas.event.world.BlockChangeByPlayerEvent;
import net.avicus.atlas.event.world.BlockChangeEvent;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.shop.PlayerEarnPointEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableEventInfo;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableObjective;
import net.avicus.atlas.sets.competitve.objectives.destroyable.event.DestroyableDamageEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.event.DestroyableRepairEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.event.DestroyableTouchEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.monument.event.MonumentDestroyEvent;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedText;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.grave.event.PlayerDeathEvent;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.MaterialData;

public class MonumentListener implements Listener {

  private final ObjectivesModule module;
  private final List<MonumentObjective> objectives;

  public MonumentListener(ObjectivesModule module, List<MonumentObjective> objectives) {
    this.module = module;
    this.objectives = objectives;
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void blockChangeByPlayer(BlockChangeByPlayerEvent event) {
    this.onBlockChange(event);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockChange(BlockChangeEvent event) {
    for (MonumentObjective objective : this.objectives) {
      if (!objective.isInside(event.getBlock())) {
        continue;
      }

      if (event instanceof BlockChangeByPlayerEvent) {
        boolean blockBreak = event.getCause() instanceof BlockBreakEvent;

        if (objective.isDestroyable()) {
          blockBreak = blockBreak || event.isToAir();
        }

        if (blockBreak) {
          handleBlockBreak(objective, (BlockChangeByPlayerEvent) event);
        } else if (event.getCause() instanceof BlockPlaceEvent) {
          handleBlockPlace(objective, (BlockChangeByPlayerEvent) event);
        } else {
          event.setCancelled(true);
        }
      } else {
        event.setCancelled(true);
      }
    }
  }

  private void handleBlockBreak(MonumentObjective objective, BlockChangeByPlayerEvent event) {
    event.setCancelled(true);

    Player player = event.getPlayer();
    Competitor competitor = this.module.getMatch().getRequiredModule(GroupsModule.class)
        .getCompetitorOf(player).orElse(null);

    if (competitor == null) {
      return;
    }

    if (!objective.canPlayerBreak(player, event.getBlock())) {
      player.sendMessage(Messages.ERROR_OBJECTIVE_DAMAGE.with(ChatColor.RED));
      return;
    }

    if (!objective.canComplete(competitor)) {
      player.sendMessage(Messages.ERROR_OBJECTIVE_DAMAGE_OWN.with(ChatColor.RED));
      return;
    }

    DestroyableEventInfo info = new DestroyableEventInfo(player, player.getItemInHand(),
        event.getBlock().getType(), event.getCause() instanceof BlockBreakEvent);

    DestroyableTouchEvent callTouch = new DestroyableTouchEvent(objective, info);
    Events.call(callTouch);
    Events.call(new PlayerEarnPointEvent(event.getPlayer(), "destroyable-touch"));

    Block block = event.getBlock();

    SingleMaterialMatcher matcher = objective.getCompletedState();
    MaterialData material = matcher.toMaterialData();

    block.setType(material.getItemType());

    // only change the data if it's specified
    if (matcher.isDataRelevant()) {
      block.setData(material.getData());
    }

    boolean wasComplete = objective.isCompleted();
    objective.recordBreak(player);
    objective.recalculateCompletion();

    if (!wasComplete) {
      if (objective.isCompleted()) {
        objective.setTouchedRecently(player, true);

        if (objective.getOwner().isPresent()) {
          this.module
              .broadcastCompletion(objective, objective.getOwner().get(), Optional.of(player));
        } else {
          this.module.broadcastCompletion(objective, player);
        }

        objective.spawnFirework(block, competitor);

        MonumentDestroyEvent destroyEvent = new MonumentDestroyEvent(objective, info);
        Events.call(destroyEvent);
        Events.call(new PlayerEarnPointEvent(event.getPlayer(), "monument-destroy"));
      } else {
        if (!objective.hasTouchedRecently(player)) {
          objective.setTouchedRecently(player, true);

          Localizable monName;

          if (objective.getOwner().isPresent()) {
            monName = objective.getName().toText(objective.getOwner().get().getChatColor());
          } else {
            monName = objective.getName().toText(competitor.getChatColor());
          }

          UnlocalizedText playerName = new UnlocalizedText(player.getName(),
              competitor.getChatColor());

          LocalizedText broadcast = Messages.GENERIC_OBJECTIVE_DAMAGED.with(monName, playerName);
          broadcast = Messages.UI_IMPORTANT.with(TextStyle.ofBold(), broadcast);

          List<Player> toMessage = new ArrayList<>();
          GroupsModule groups = module.getMatch().getRequiredModule(GroupsModule.class);
          toMessage.addAll(groups.getGroup(player).getPlayers());
          toMessage.addAll(groups.getSpectators().getPlayers());

          for (Player messaging : toMessage) {
            messaging.sendMessage(broadcast);
          }

          objective.spawnFirework(block, competitor);

          // New break message after 15 seconds.
          AtlasTask.of(() -> {
            try {
              if (objective.isTouchRelevant(player)) {
                objective.setTouchedRecently(player, false);
              }
            } catch (Exception e) {
              // The match has likely cycled, just ignore it.
            }
          }).later(15 * 20);
        }

        DestroyableDamageEvent damageEvent = new DestroyableDamageEvent(objective, info);
        Events.call(damageEvent);
        Events.call(new PlayerEarnPointEvent(event.getPlayer(), "destroyable-damage"));
      }
    }

    if (objective.getPointsPerBlock().isPresent()) {
      this.module.score(competitor, objective.getPointsPerBlock().get(), player);
    }

    if (objective.isCompleted() && objective.getPoints().isPresent()) {
      this.module.score(competitor, objective.getPoints().get(), player);
    }
  }

  private void handleBlockPlace(MonumentObjective objective, BlockChangeByPlayerEvent event) {
    Player player = event.getPlayer();
    Group group = this.module.getMatch().getRequiredModule(GroupsModule.class).getGroup(player);

    if (!objective.shouldEnforceRepairRules()) {
      return;
    }

    if (!objective.isOwner(group)) {
      player.sendMessage(Messages.ERROR_OBJECTIVE_REPAIR_ENEMY.with(ChatColor.RED));
      event.setCancelled(true);
      return;
    }

    if (!objective.isRepairable() || !objective.canPlayerRepair(player, event.getBlock())) {
      player.sendMessage(Messages.ERROR_OBJECTIVE_CANNOT_REPAIR.with(ChatColor.RED));
      event.setCancelled(true);
      return;
    }

    BlockState placed = event.getNewState();

    if (!objective.getMaterials().matches(placed) && !objective.isAnyRepair()) {
      player.sendMessage(Messages.ERROR_OBJECTIVE_BAD_REPAIR.with(ChatColor.RED));
      event.setCancelled(true);
      return;
    }

    Localizable monName;

    if (objective.getOwner().isPresent()) {
      monName = objective.getName().toText(objective.getOwner().get().getChatColor());
    } else {
      monName = objective.getName().toText(group.getChatColor());
    }

    UnlocalizedText playerName = new UnlocalizedText(player.getName(), group.getChatColor());

    LocalizedText broadcast = Messages.GENERIC_OBJECTIVE_REPAIRED.with(monName, playerName);
    broadcast = Messages.UI_IMPORTANT.with(TextStyle.ofBold(), broadcast);
    this.module.getMatch().broadcast(broadcast);

    objective.repair(event.getBlock());
    event.setCancelled(false);

    new AtlasTask() {
      @Override
      public void run() {
        objective.recalculateCompletion();

        DestroyableRepairEvent callRepair = new DestroyableRepairEvent(objective, player);
        Events.call(callRepair);
        Events.call(new PlayerEarnPointEvent(event.getPlayer(), "destroyable-repair"));
      }
    }.now();
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    for (DestroyableObjective objective : this.objectives) {
      objective.setTouchedRecently(event.getPlayer(), false);
    }
  }
}
