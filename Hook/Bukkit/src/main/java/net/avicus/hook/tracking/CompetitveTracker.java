package net.avicus.hook.tracking;

import java.util.Collection;
import java.util.Date;
import net.avicus.atlas.sets.competitve.objectives.destroyable.leakable.event.LeakableLeakEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.monument.event.MonumentDestroyEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagCaptureEvent;
import net.avicus.atlas.sets.competitve.objectives.hill.event.HillCaptureEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPlaceEvent;
import net.avicus.hook.Hook;
import net.avicus.hook.HookConfig;
import net.avicus.hook.utils.HookTask;
import net.avicus.magma.database.model.impl.ObjectiveCompletion;
import net.avicus.magma.database.model.impl.ObjectiveType;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CompetitveTracker implements Listener {

  private final Tracking tracking;

  private final ObjectiveType monument;
  private final ObjectiveType flag;
  private final ObjectiveType hill;
  private final ObjectiveType wool;
  private final ObjectiveType leakable;

  public CompetitveTracker(Tracking tracking) {
    this.tracking = tracking;
    this.monument = Hook.database().getObjectiveTypes().findOrCreate("monument");
    this.flag = Hook.database().getObjectiveTypes().findOrCreate("flag");
    this.hill = Hook.database().getObjectiveTypes().findOrCreate("hill");
    this.wool = Hook.database().getObjectiveTypes().findOrCreate("wool");
    this.leakable = Hook.database().getObjectiveTypes().findOrCreate("leakable");
  }

  protected static void objective(Collection<Player> players, ObjectiveType type) {
    players.stream().forEach((p) -> objective(p, type));
  }

  protected static void objective(Player player, ObjectiveType type) {
    if (!HookConfig.Tracking.isObjectives()) {
      return;
    }

    User user = Users.user(player);
    ObjectiveCompletion objective = new ObjectiveCompletion(user.getId(), type, new Date());
    HookTask.of(() -> Hook.database().getObjectiveCompletions().insert(objective).execute())
        .nowAsync();
  }

  @EventHandler
  public void onMonumentDestroy(MonumentDestroyEvent event) {
    objective(event.getPlayers(), monument);
  }

  @EventHandler
  public void onFlagCapture(FlagCaptureEvent event) {
    objective(event.getPlayers(), flag);
  }

  @EventHandler
  public void onFlagCapture(HillCaptureEvent event) {
    objective(event.getPlayers(), hill);
  }

  @EventHandler
  public void onWoolPlace(WoolPlaceEvent event) {
    objective(event.getPlayers().get(0), wool);
  }

  @EventHandler
  public void onLeakableLeak(LeakableLeakEvent event) {
    objective(event.getPlayers().get(0), leakable);
  }
}
