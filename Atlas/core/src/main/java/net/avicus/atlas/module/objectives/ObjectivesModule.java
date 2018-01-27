package net.avicus.atlas.module.objectives;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.BridgeableModule;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.bridge.ObjectivesModuleBridge;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.entity.EntityListener;
import net.avicus.atlas.module.objectives.entity.EntityObjective;
import net.avicus.atlas.module.objectives.score.ScoreListener;
import net.avicus.atlas.module.objectives.score.ScoreObjective;
import net.avicus.atlas.util.Events;
import net.avicus.compendium.number.NumberAction;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@ToString(exclude = "match")
public class ObjectivesModule extends BridgeableModule<ObjectivesModuleBridge> implements Module {

  @Getter
  private final Match match;
  @Getter
  private final List<Objective> objectives;
  @Getter
  private final List<Listener> listeners;
  @Getter
  private final List<ScoreObjective> scores;

  public ObjectivesModule(Match match, List<Objective> objectives) {
    this.match = match;
    this.objectives = objectives;
    this.listeners = new ArrayList<>();
    this.scores = getObjectivesByType(ScoreObjective.class);
    buildBridges(this);
  }

  public void broadcastCompletion(Objective objective, Player player) {
    broadcastCompletion(objective,
        this.match.getRequiredModule(GroupsModule.class).getGroup(player), Optional.of(player));
  }

  public void broadcastCompletion(Objective objective, Group group) {
    broadcastCompletion(objective, group, Optional.empty());
  }

  public void broadcastCompletion(Objective objective, Group group, Optional<Player> cause) {
    for (ObjectivesModuleBridge bridge : getBridges().values()) {
      if (bridge.broadcastCompletion(objective, group, cause)) {
        return;
      }
    }

    throw new RuntimeException("No bridges handled completion broadcasts!");
  }

  public <T extends Objective> List<T> getObjectivesByType(Class<? extends Objective> clazz) {
    return this.objectives.stream()
        .filter(objective -> clazz.isAssignableFrom(objective.getClass()))
        .map(objective -> (T) objective)
        .collect(Collectors.toList());
  }

  @Override
  public void open() {
    List<EntityObjective> entity = getObjectivesByType(EntityObjective.class);
    if (!entity.isEmpty()) {
      this.listeners.add(new EntityListener(this, entity));
    }
    List<ScoreObjective> scores = getObjectivesByType(ScoreObjective.class);
    if (scores.size() > 0) {
      listeners.add(new ScoreListener(match, scores));
    }

    this.objectives.forEach(Objective::initialize);
    getBridges().values().forEach(b -> b.onOpen(this));
    this.listeners.forEach(Events::register);
  }

  @Override
  public void close() {
    this.listeners.forEach(Events::unregister);
    getBridges().values().forEach(b -> b.onClose(this));
  }

  public void score(Competitor competitor, int reward) {
    score(competitor, reward, (Player) null);
  }

  public void score(Competitor competitor, int reward, @Nullable Player actor) {
    score(competitor, reward, NumberAction.ADD, actor);
  }

  public void score(Competitor competitor, int reward, NumberAction action) {
    score(competitor, reward, action, null);
  }

  public void score(Competitor competitor, int reward, NumberAction action,
      @Nullable Player actor) {
    getBridges().values().forEach(b -> b.score(competitor, reward, action, actor));
    getScores().stream()
        .filter(score -> score.canComplete(competitor))
        .forEach(score -> score.modify(competitor, reward, action, actor));
  }
}
