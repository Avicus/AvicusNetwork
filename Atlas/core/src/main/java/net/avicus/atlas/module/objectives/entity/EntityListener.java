package net.avicus.atlas.module.objectives.entity;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.avicus.atlas.event.world.EntityChangeEvent;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.EntityVariable;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.shop.PlayerEarnPointEvent;
import net.avicus.atlas.util.Events;
import net.avicus.grave.event.EntityDeathEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDespawnInVoidEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import tc.oc.tracker.event.EntityDamageEvent;

public class EntityListener implements Listener {

  private final ObjectivesModule module;
  private final List<EntityObjective> objectives;

  public EntityListener(ObjectivesModule module, List<EntityObjective> objectives) {
    this.module = module;
    this.objectives = objectives;
  }

  public boolean handle(Entity e, @Nullable Player actor) {
    CheckContext context = new CheckContext(this.module.getMatch());
    context.add(new EntityVariable(e));
    if (actor != null) {
      context.add(new PlayerVariable(actor));
    }
    context.add(new LocationVariable(e.getLocation()));

    for (EntityObjective objective : this.objectives) {
      if (objective.getTrackedEntities().containsKey(e)) {
        if (objective.getDamageCheck().isPresent() && objective.getDamageCheck().get().test(context)
            .fails()) {
          return true;
        }

        objective.updateCompletion();
        if (actor != null) {
          if (module.getMatch().getRequiredModule(GroupsModule.class).isObservingOrDead(actor)) {
            return true;
          }

          Optional<Competitor> competitor = module.getMatch().getRequiredModule(GroupsModule.class)
              .getCompetitorOf(actor);
          competitor.ifPresent(c -> {
                objective.getPoints().ifPresent(p ->
                    module.score(c, p));
                Events.call(new PlayerEarnPointEvent(actor, "entity-destroy"));
              }
          );
        }
        break;
      }
    }

    return false;
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onDamage(EntityDamageEvent e) {
    if (e.getDamage() > e.getEntity().getHealth()) {
      return; // has died, let death event handle
    }

    Player actor = null;
    if (e.getInfo().getResolvedDamager() instanceof Player) {
      actor = (Player) e.getInfo().getResolvedDamager();
    }
    e.setCancelled(handle(e.getEntity(), actor));
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onChange(EntityChangeEvent e) {
    if (e.getWhoChanged() instanceof Player) {
      e.setCancelled(handle(e.getEntity(), (Player) e.getWhoChanged()));
    } else {
      e.setCancelled(handle(e.getEntity(), null));
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onItemPickup(PlayerPickupItemEvent e) {
    e.setCancelled(handle(e.getItem(), e.getPlayer()));
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onDeath(EntityDeathEvent e) {
    Player actor = null;
    if (e.getLifetime().getLastDamage() != null && e.getLifetime().getLastDamage().getInfo()
        .getResolvedDamager() instanceof Player) {
      actor = (Player) e.getLifetime().getLastDamage().getInfo().getResolvedDamager();
    }
    handle(e.getEntity(), actor);
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onDespawn(EntityDespawnInVoidEvent e) {
    handle(e.getEntity(), null);
  }
}
