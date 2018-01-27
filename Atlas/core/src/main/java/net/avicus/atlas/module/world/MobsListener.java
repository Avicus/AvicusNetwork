package net.avicus.atlas.module.world;

import java.util.Optional;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.EntityVariable;
import net.avicus.atlas.module.checks.variable.SpawnReasonVariable;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.states.StatesModule;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import tc.oc.tracker.Trackers;
import tc.oc.tracker.trackers.OwnedMobTracker;

public class MobsListener implements Listener {

  private final Match match;
  private final Optional<Check> mobs;

  private final OwnedMobTracker tracker = Trackers.getTracker(OwnedMobTracker.class);

  public MobsListener(Match match, Optional<Check> mobs) {
    this.match = match;
    this.mobs = mobs;
  }

  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent event) {
    if (event.getSpawnReason() == SpawnReason.CUSTOM
        || event.getSpawnReason() == SpawnReason.DEFAULT) {
      return;
    }

    event.setCancelled(true);

    // never allow spawning during non-playing states
    if (!this.match.getRequiredModule(StatesModule.class).getState().isPlaying()) {
      return;
    }

    // by default, all mobs disabled
    if (!this.mobs.isPresent()) {
      return;
    }

    CheckContext context = new CheckContext(match);
    context.add(new EntityVariable(event.getEntity()));
    context.add(new SpawnReasonVariable(event.getSpawnReason()));

    event.setCancelled(this.mobs.get().test(context).fails());
  }

  @EventHandler
  public void onTarget(EntityTargetEvent event) {
    if (!(event.getTarget() instanceof Player)) {
      return;
    }

    Player targetPlayer = (Player) event.getTarget();
    event.setCancelled(preventTeamMob(event.getEntity(), targetPlayer));
  }

  @EventHandler
  public void onDamage(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    if (event.getDamager() instanceof Player && match.getRequiredModule(GroupsModule.class)
        .isObservingOrDead(((Player) event.getDamager()))) {
      return;
    }

    Player targetPlayer = (Player) event.getEntity();
    event.setCancelled(preventTeamMob(event.getDamager(), targetPlayer));
  }

  private boolean preventTeamMob(Entity entity, Player effected) {
    if (!(entity instanceof Creature || entity instanceof Slime || entity instanceof Ghast)) {
      return false;
    }

    if (match.getRequiredModule(GroupsModule.class).isObservingOrDead(effected)) {
      return true;
    }

    Player ownerPlayer = tracker.getOwner((LivingEntity) entity);
    if (ownerPlayer == null) {
      return false;
    }

    Optional<Competitor> target = match.getRequiredModule(GroupsModule.class)
        .getCompetitorOf(effected);
    Optional<Competitor> owner = match.getRequiredModule(GroupsModule.class)
        .getCompetitorOf(ownerPlayer);

    return owner.isPresent() && target.isPresent() && owner.get().equals(target.get());
  }
}
