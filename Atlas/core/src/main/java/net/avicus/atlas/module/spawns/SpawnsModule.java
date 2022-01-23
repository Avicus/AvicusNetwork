package net.avicus.atlas.module.spawns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.avicus.atlas.event.player.PlayerSpawnBeginEvent;
import net.avicus.atlas.event.player.PlayerSpawnCompleteEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.GroupVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.atlas.runtimeconfig.RuntimeConfigurable;
import net.avicus.atlas.runtimeconfig.fields.ConfigurableField;
import net.avicus.atlas.runtimeconfig.fields.DurationField;
import net.avicus.atlas.runtimeconfig.fields.SimpleFields.BooleanField;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Players;
import net.avicus.magma.network.server.Servers;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.joda.time.Duration;
import org.joda.time.Instant;

@ToString(exclude = "match")
public class SpawnsModule implements Module, RuntimeConfigurable {

  @Getter
  private final Match match;
  private final SpawnListener listener;
  private final List<Spawn> spawns;
  private final Map<Player, Boolean> deadPlayers;
  private final List<Player> spawningPlayers;
  private final Map<Player, RespawnTask> respawnTasks;
  @Getter
  private Duration respawnDelay;
  private boolean respawnFreeze;
  @Getter
  @Setter
  private boolean autoRespawn;
  private boolean respawnBlindness;

  public SpawnsModule(Match match, List<Spawn> spawns, Duration respawnDelay, boolean autoRespawn,
      boolean respawnFreeze, boolean respawnBlindness) {
    this.match = match;
    this.listener = new SpawnListener(this);
    this.spawns = spawns;
    this.deadPlayers = new HashMap<>();
    this.spawningPlayers = new ArrayList<>();
    this.respawnTasks = new HashMap<>();
    this.respawnDelay = respawnDelay;
    this.autoRespawn = autoRespawn;
    this.respawnFreeze = respawnFreeze;
    this.respawnBlindness = respawnBlindness;
  }

  @Override
  public void open() {
    Events.register(this.listener);
  }

  @Override
  public void close() {
    Events.unregister(this.listener);

    this.respawnTasks.values().forEach(RespawnTask::cancel0);
  }

  public void spawn(Group group, Player player, boolean giveLoadout, boolean teleportPlayer) {
    this.spawningPlayers.add(player);
    Spawn spawn = getSpawn(group, player);

    Players.reset(player);

    PlayerSpawnBeginEvent call = new PlayerSpawnBeginEvent(player, group, spawn, giveLoadout,
        teleportPlayer);
    Events.call(call);

    if (call.isTeleportPlayer()) {
      player.teleport(spawn.selectLocation(this.match, player));
    }

    if (call.getGroup().isObserving()) {
      player.getInventory().setItem(8, Servers.createMenuOpener(player));
    }

    Optional<Loadout> loadout = call.getSpawn().getLoadout();
    if (call.isGiveLoadout() && loadout.isPresent()) {
      loadout.get().apply(player);
    }

    PlayerSpawnCompleteEvent completeEvent = new PlayerSpawnCompleteEvent(call);
    Events.call(completeEvent);
    this.spawningPlayers.remove(player);
  }

  public void spawn(Player player) {
    spawn(player, true);
  }

  public void spawn(Player player, boolean loadout) {
    Group group = this.match.getRequiredModule(GroupsModule.class).getGroup(player);
    spawn(group, player, loadout, true);
  }

  public Spawn getSpawn(Group group, Player player) {
    List<Spawn> found = new ArrayList<>();
    for (Spawn spawn : this.spawns) {
      if (spawn.getCheck().isPresent()) {
        CheckContext context = new CheckContext(this.match);
        context.add(new GroupVariable(group));
        context.add(new PlayerVariable(player));
        if (spawn.getCheck().get().test(context).fails()) {
          continue;
        }
      }

      if (spawn.getGroup().isPresent()) {
        if (spawn.getGroup().get().equals(group)) {
          return spawn;
        } else {
          continue;
        }
      }
      found.add(spawn);
    }
    if (!found.isEmpty()) {
      return found.get(0);
    }
    throw new RuntimeException("Spawn not found for group.");
  }

  public void startRespawnTask(Player player) {
    Instant when = Instant.now().plus(this.respawnDelay);
    RespawnTask task = new RespawnTask(
        this,
        player,
        when,
        this.autoRespawn,
        this.respawnFreeze,
        this.respawnBlindness
    );
    this.respawnTasks.put(player, task.start());
  }

  public void stopRespawnTask(Player player) {
    stopRespawnTask(player, true);
  }

  public void stopRespawnTask(Player player, boolean unDead) {
    RespawnTask task = this.respawnTasks.remove(player);
    if (task != null) {
      task.cancel0();
    }
    if (unDead) {
      setDead(player, false);
      this.match.getRequiredModule(GroupsModule.class).refreshObservers();
    }
  }

  public void setDead(Player player, boolean dead) {
    if (dead) {
      this.deadPlayers.put(player, true);
    } else {
      this.deadPlayers.remove(player);
    }
  }

  public boolean isRespawning(Player player) {
    return this.respawnTasks.containsKey(player);
  }

  public boolean isDead(Player player) {
    return this.deadPlayers.containsKey(player);
  }

  public boolean isSpawning(Player player) {
    return this.spawningPlayers.contains(player);
  }

  public void queueAutoRespawn(Player player) {
    RespawnTask task = this.respawnTasks.get(player);

    if (task == null) {
      return;
    }

    task.setAutoRespawn(true);
  }

  @Override
  public ConfigurableField[] getFields() {
    return new ConfigurableField[]{
        new DurationField("Respawn Delay", () -> this.respawnDelay, (v) -> this.respawnDelay = v),
        new BooleanField("Respawn Freeze", () -> this.respawnFreeze, (v) -> this.respawnFreeze = v),
        new BooleanField("Auto Respawn", () -> this.autoRespawn, (v) -> this.autoRespawn = v),
        new BooleanField("Respawn Blindness", () -> this.respawnBlindness, (v) -> this.respawnBlindness = v)
    };
  }

  @Override
  public String getDescription(CommandSender viewer) {
    return "Spawns";
  }

  @Override
  public List<RuntimeConfigurable> getChildren() {
    return this.spawns.stream().map(s -> (RuntimeConfigurable)s).collect(Collectors.toList());
  }
}
