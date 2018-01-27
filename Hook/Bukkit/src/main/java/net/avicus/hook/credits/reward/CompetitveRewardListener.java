package net.avicus.hook.credits.reward;

import net.avicus.atlas.sets.competitve.objectives.destroyable.leakable.event.LeakableLeakEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.monument.event.MonumentDestroyEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagCaptureEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPickupEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPlaceEvent;
import net.avicus.hook.HookConfig;
import net.avicus.hook.credits.Credits;
import net.avicus.hook.utils.Messages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CompetitveRewardListener implements Listener {

  @EventHandler
  public void onMonumentDestroy(MonumentDestroyEvent event) {
    int reward = HookConfig.Credits.Rewards.getMonumentDestroy();
    if (reward > 0) {
      Credits.reward(event.getPlayers().get(0), reward, Messages.UI_REWARD_MONUMENT_DESTROYED);
    }
  }

  @EventHandler
  public void onFlagCapture(FlagCaptureEvent event) {
    int reward = HookConfig.Credits.Rewards.getFlagCapture();
    if (reward > 0) {
      Credits.reward(event.getPlayers().get(0), reward, Messages.UI_REWARD_FLAG_CAPTURED);
    }
  }

  @EventHandler
  public void onWoolPlace(WoolPlaceEvent event) {
    int reward = HookConfig.Credits.Rewards.getWoolPlace();
    if (reward > 0) {
      Credits.reward(event.getPlayers().get(0), reward, Messages.UI_REWARD_WOOL_PLACE);
    }
  }

  @EventHandler
  public void onWoolPickup(WoolPickupEvent event) {
    int reward = HookConfig.Credits.Rewards.getWoolPickup();
    if (reward > 0) {
      Credits.reward(event.getPlayer(), reward, Messages.UI_REWARD_WOOL_PICKUP);
    }
  }

  @EventHandler
  public void onLeakableLeak(LeakableLeakEvent event) {
    int reward = HookConfig.Credits.Rewards.getLeakableLeak();
    if (reward > 0) {
      Credits.reward(event.getPlayers().get(0), reward, Messages.UI_REWARD_LEAKABLE_LEAK);
    }
  }
}
