package net.avicus.atlas.module.stats.action.lifetime;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.stats.action.base.Action;
import net.avicus.atlas.module.stats.action.lifetime.type.CompetitorLifetime;
import net.avicus.atlas.module.stats.action.lifetime.type.MatchLifetime;
import net.avicus.atlas.module.stats.action.lifetime.type.ObjectiveLifetime;
import net.avicus.atlas.module.stats.action.lifetime.type.PlayerLifetime;
import org.bukkit.entity.Player;

@Getter
@ToString
public class LifetimeStore {

  // Lifetimes
  private final MatchLifetime matchLifetime;
  private final ArrayListMultimap<UUID, PlayerLifetime> playerLifetimes = ArrayListMultimap
      .create();
  private final HashMap<Competitor, CompetitorLifetime> competitorLifetimes = Maps.newHashMap();
  private final HashMap<Objective, ObjectiveLifetime> objectiveLifetimes = Maps.newHashMap();

  public LifetimeStore(Match match) {
    this.matchLifetime = new MatchLifetime(Instant.now(), match);
    match.getModule(ObjectivesModule.class).ifPresent(objectivesModule -> {
      objectivesModule.getObjectives().forEach(objective -> {
        this.objectiveLifetimes.put(objective, new ObjectiveLifetime(Instant.now(), objective));
      });
    });
    match.getModule(GroupsModule.class).ifPresent(groupsModule -> {
      groupsModule.getCompetitors().forEach(competitor -> {
        this.competitorLifetimes.put(competitor, new CompetitorLifetime(Instant.now(), competitor));
      });
    });
    match.getPlayers().forEach(this::restartLifetime);
  }

  public PlayerLifetime getCurrentLifetime(Player player, boolean create) {
    if (this.playerLifetimes.containsKey(player.getUniqueId())) {
      return this.playerLifetimes.get(player.getUniqueId())
          .get(this.playerLifetimes.get(player.getUniqueId()).size() - 1);
    }

    if (create) {
      PlayerLifetime lifetime = restartLifetime(player);
      this.playerLifetimes.put(player.getUniqueId(), lifetime);
      return lifetime;
    }

    return null;
  }

  public ObjectiveLifetime getCurrentLifetime(Objective objective) {
    if (this.objectiveLifetimes.containsKey(objective)) {
      return this.objectiveLifetimes.get(objective);
    }

    ObjectiveLifetime newLife = new ObjectiveLifetime(Instant.now(), objective);
    this.objectiveLifetimes.put(objective, newLife);
    return newLife;
  }

  public CompetitorLifetime getCurrentLifetime(Competitor competitor) {
    if (this.competitorLifetimes.containsKey(competitor)) {
      return this.competitorLifetimes.get(competitor);
    }

    CompetitorLifetime newLife = new CompetitorLifetime(Instant.now(), competitor);
    this.competitorLifetimes.put(competitor, newLife);
    return newLife;
  }

  public PlayerLifetime restartLifetime(Player player) {
    PlayerLifetime lifetime = this.getCurrentLifetime(player, false);
    if (lifetime != null) {
      lifetime.end();
    }

    PlayerLifetime newLife = new PlayerLifetime(Instant.now(), player.getUniqueId());

    this.playerLifetimes.put(player.getUniqueId(), newLife);
    return newLife;
  }

  public double getScore(UUID uuid) {
    return this.getPlayerLifetimes().get(uuid).stream().flatMap(l -> l.getActions().stream())
        .mapToDouble(Action::getScore).sum();
  }

  @Nullable
  public <N, C extends Action> N mostCommonAttribute(Class<? extends C> actionClass,
      Function<C, N> refMethod) {
    Multiset<N> commons = HashMultiset.create();
    this.getPlayerLifetimes().values().stream()
        .flatMap(listContainer -> listContainer.getActions().stream())
        .collect(Collectors.toList())
        .stream().filter(act -> act.getClass().equals(actionClass))
        .forEach(action -> commons.add(refMethod.apply((C) action)));

    return commons.entrySet()
        .stream()
        .max(Comparator.comparing(Multiset.Entry::getCount)).map(Multiset.Entry::getElement)
        .orElse(null);
  }

  @Nullable
  public <N, C extends Action> N mostCommonAttribute(UUID actor, Class<? extends C> actionClass,
      Function<C, N> refMethod) {
    Multiset<N> commons = HashMultiset.create();
    this.getPlayerLifetimes().get(actor).stream()
        .flatMap(listContainer -> listContainer.getActions().stream())
        .collect(Collectors.toList())
        .stream().filter(act -> act.getClass().equals(actionClass))
        .forEach(action -> commons.add(refMethod.apply((C) action)));

    return commons.entrySet()
        .stream()
        .max(Comparator.comparing(Multiset.Entry::getCount)).map(Multiset.Entry::getElement)
        .orElse(null);
  }
}
