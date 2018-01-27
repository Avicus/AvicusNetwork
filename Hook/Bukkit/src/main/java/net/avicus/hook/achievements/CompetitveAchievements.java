package net.avicus.hook.achievements;

import net.avicus.atlas.sets.competitve.objectives.destroyable.event.DestroyableTouchEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.leakable.LeakableObjective;
import net.avicus.atlas.sets.competitve.objectives.destroyable.leakable.event.LeakableLeakEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.monument.MonumentObjective;
import net.avicus.atlas.sets.competitve.objectives.destroyable.monument.event.MonumentDestroyEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagCaptureEvent;
import net.avicus.atlas.sets.competitve.objectives.hill.event.HillCaptureEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPickupEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPlaceEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CompetitveAchievements implements Listener {

  private final Achievements achievements;

  public CompetitveAchievements(Achievements achievements) {
    this.achievements = achievements;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onMonumentDestroy(MonumentDestroyEvent event) {
    achievements.increment("monument-completions", event.getPlayers().get(0));
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDamage(DestroyableTouchEvent event) {
    if (event.getObjective() instanceof MonumentObjective) {
      achievements.increment("monument-touches", event.getPlayer());
    } else if (event.getObjective() instanceof LeakableObjective) {
      achievements.increment("leakable-touches", event.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onFlagCapture(FlagCaptureEvent event) {
    achievements.increment("flag-captures", event.getPlayers().get(0));
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onHillCapture(HillCaptureEvent event) {
    achievements.increment("hill-captures", event.getPlayers().get(0));
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onWoolPlace(WoolPlaceEvent event) {
    achievements.increment("wool-places", event.getPlayers().get(0));
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPickup(WoolPickupEvent event) {
    achievements.increment("wool-touches", event.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onLeakableLeak(LeakableLeakEvent event) {
    achievements.increment("leakable-leaks", event.getPlayers().get(0));
  }
}
