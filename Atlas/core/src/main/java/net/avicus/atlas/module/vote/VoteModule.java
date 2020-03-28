package net.avicus.atlas.module.vote;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.ToString;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.countdown.CyclingCountdown;
import net.avicus.atlas.event.player.PlayerSpawnBeginEvent;
import net.avicus.atlas.map.rotation.Rotation;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchBuildException;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.countdown.CountdownManager;
import net.avicus.compendium.plugin.CompendiumPlugin;
import net.avicus.compendium.utils.Strings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.joda.time.Seconds;

@ToString(exclude = "match")
public final class VoteModule implements Module {

  final Match match;
  private final Map<Integer, Match> options = Maps.newHashMap();
  private final Map<UUID, Match> votes = Maps.newHashMap();
  boolean started;
  private VoteCountdown countdown;

  VoteModule(final Match match) {
    this.match = match;
  }

  static Map<Integer, Match> parse(final List<String> raw) {
    final AtomicInteger index = new AtomicInteger(1);
    return Maps.newHashMap(raw.stream()
        .map(s -> Atlas.get().getMapManager().search(s).orElse(null))
        .map(m -> {
          try {
            return Atlas.get().getMatchManager().getFactory().create(m);
          } catch (MatchBuildException | ModuleBuildException e) {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(o -> index.getAndIncrement(), Function.identity())));
  }

  public void start(final Map<Integer, Match> options, String rawTime, boolean onCycle) {
    this.options.clear();
    this.options.putAll(options);
    this.votes.clear();
    this.countdown = new VoteCountdown(this.match,
        rawTime == null ? Seconds.seconds(45).toStandardDuration() : Strings.toDuration(rawTime),
        this);
    if (onCycle) {
      Atlas.get().getMatchManager().getRotation().setVoteQueued(true);
      return;
    }

    this.delayedStart();
  }

  boolean cancel() {
    if (this.countdown == null) {
      return false;
    }

    this.options.clear();

    final Rotation rotation = Atlas.get().getMatchManager().getRotation();
    rotation.setVoteQueued(false);

    if (this.started) {
      final CountdownManager cm = CompendiumPlugin.getInstance().getCountdownManager();
      cm.cancel(this.countdown);
    }

    Optional<Match> nm = rotation.getNextMatch();
    if (nm.isPresent()) {
      rotation.cycleMatch(new CyclingCountdown(this.match, nm.get()));
    } else {
      try {
        final Match match = Atlas.get().getMatchManager().getFactory().create(
            Atlas.get().getMapManager().getLibraries().iterator().next().getMaps().iterator()
                .next());
        rotation.next(match, true);
        rotation.cycleMatch(new CyclingCountdown(this.match, match,
            Optional.of(Seconds.seconds(15).toStandardDuration())));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    this.countdown = null;
    this.started = false;

    return true;
  }

  public void delayedStart() {
    if (this.countdown == null) {
      return;
    }

    Atlas.get().getMatchManager().getRotation().cancelAllAndStart(this.countdown);
    this.started = true;
    for (Player player : this.match.getPlayers()) {
      ItemStack stack = VoteMenu.create(player);
      for (ItemStack content : player.getInventory().getContents()) {
        if (content != null && content.isSimilar(stack)) {
          stack = null;
          break;
        }
      }
      if (stack != null) {
        player.getInventory().addItem(stack);
      }
    }
    this.match.broadcast(Messages.VOTE_START.with(ChatColor.YELLOW));
  }

  Collection<Match> votes() {
    return this.votes.values();
  }

  void cast(final Player player, final Match vote) {
    if (this.votes.containsKey(player.getUniqueId()) &&
        this.votes.get(player.getUniqueId()).getId().equals(match.getId())) return;

    Events.call(new PlayerCastVoteEvent(player));
    this.votes.put(player.getUniqueId(), vote);
  }

  boolean isCast(final Player player, final Match vote) {
    return this.votes.get(player.getUniqueId()) == vote;
  }

  @Nullable
  final Match getOptional(final int index) {
    return this.options.get(index);
  }

  final Map<Integer, Match> getOptions() {
    return Collections.unmodifiableMap(this.options);
  }

  @EventHandler
  public void playerInteract(final PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_AIR
        && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (VoteMenu.matches(event.getItem())) {
      VoteMenu.create(this, event.getPlayer()).open();
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerSpawn(PlayerSpawnBeginEvent event) {
    final Player player = event.getPlayer();
    if (event.getGroup().isObserving() && event.isGiveLoadout() && !this.options.isEmpty()) {
      player.getInventory().addItem(VoteMenu.create(event.getPlayer()));
    }
  }
}
