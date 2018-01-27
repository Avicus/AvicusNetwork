package net.avicus.hook.prestige;

import java.util.HashMap;
import java.util.Map;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.event.match.MatchCompleteEvent;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableObjective;
import net.avicus.atlas.sets.competitve.objectives.destroyable.event.DestroyableDamageEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.leakable.event.LeakableLeakEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.monument.event.MonumentDestroyEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagCaptureEvent;
import net.avicus.atlas.sets.competitve.objectives.hill.HillObjective;
import net.avicus.atlas.sets.competitve.objectives.hill.event.HillCaptureEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPickupEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPlaceEvent;
import net.avicus.hook.HookConfig;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.module.prestige.PrestigeModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CompetitveRewardListener implements Listener {

  private final PrestigeModule module;
  private final Map<HillObjective, Competitor> hillOwners;

  public CompetitveRewardListener(PrestigeModule module) {
    this.module = module;
    this.hillOwners = new HashMap<>();
  }

  @EventHandler
  public void onMonumentDestroy(MonumentDestroyEvent event) {
    int reward = HookConfig.Experience.Rewards.getMonumentDestroy();
    if (reward > 0) {
      this.module
          .reward(event.getPlayers().get(0), reward, Messages.UI_REWARD_MONUMENT_DESTROYED,
              Atlas.getMatch().getMap().getGenre().name());
    }

  }

  @EventHandler
  public void onFlagCapture(FlagCaptureEvent event) {
    int reward = HookConfig.Experience.Rewards.getFlagCapture();
    if (reward > 0) {
      this.module.reward(event.getPlayers().get(0), reward, Messages.UI_REWARD_FLAG_CAPTURED,
          Atlas.getMatch().getMap().getGenre().name());
    }
  }

  @EventHandler
  public void onDestroyableDamage(DestroyableDamageEvent event) {
    int reward = HookConfig.Experience.Rewards.getMonumentDamage();
    if (reward > 0) {
      int blocks = ((DestroyableObjective) event.getObjective()).getOriginals().size();
      if (blocks > reward) {
        reward = Math.max(1, (reward / blocks) * reward);
      }

      this.module.reward(event.getInfo().getActor(), reward, Messages.UI_REWARD_MONUMENT_DAMAGED,
          Atlas.getMatch().getMap().getGenre().name());
    }
  }

  @EventHandler
  public void onWoolPlace(WoolPlaceEvent event) {
    int reward = HookConfig.Experience.Rewards.getWoolPlace();
    if (reward > 0) {
      this.module.reward(event.getPlayers().get(0), reward, Messages.UI_REWARD_WOOL_PLACE,
          Atlas.getMatch().getMap().getGenre().name());
    }
  }

  @EventHandler
  public void onWoolPickup(WoolPickupEvent event) {
    int reward = HookConfig.Experience.Rewards.getWoolPickup();
    if (reward > 0) {
      this.module.reward(event.getPlayer(), reward, Messages.UI_REWARD_WOOL_PICKUP,
          Atlas.getMatch().getMap().getGenre().name());
    }
  }

  @EventHandler
  public void onLeakableLeak(LeakableLeakEvent event) {
    int reward = HookConfig.Experience.Rewards.getLeakableLeak();
    if (reward > 0) {
      this.module.reward(event.getPlayers().get(0), reward, Messages.UI_REWARD_LEAKABLE_LEAK,
          Atlas.getMatch().getMap().getGenre().name());
    }
  }

  @EventHandler
  public void onHillCapture(HillCaptureEvent event) {
    if (!event.getNewOwner().isPresent()) {
      return;
    }

    HillObjective objective = (HillObjective) event.getObjective();

    // This means the same team capped the hill again, don't reward XP.
    if (this.hillOwners.containsKey(objective) && this.hillOwners.get(objective)
        .equals(event.getNewOwner().orElse(null))) {
      return;
    }

    this.hillOwners.put(objective, event.getNewOwner().orElse(null));
    int reward = HookConfig.Experience.Rewards.getLeakableLeak();
    if (reward > 0) {
      event.getPlayers()
          .forEach(p -> this.module.reward(p, reward, Messages.UI_REWARD_HILL_CAPTURE,
              Atlas.getMatch().getMap().getGenre().name()));
    }
  }

  // TODO: Score box

  @EventHandler
  public void onComplete(MatchCompleteEvent event) {
    this.hillOwners.clear();
  }
}
