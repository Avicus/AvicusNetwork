package net.avicus.atlas.module.world;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.event.match.MatchLoadEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.executors.ExecutorsFactory;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.util.Events;
import net.avicus.compendium.utils.NullChunkGenerator;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEntityEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

@ToString(exclude = "match")
public class WorldModule implements Module {

  private static final NullChunkGenerator NULL_GENERATOR = new NullChunkGenerator();
  private final Match match;
  private final Map<GameRule, String> gamerules;
  private final Optional<Check> mobs;
  private final Optional<Difficulty> difficulty;
  private final List<Listener> listeners;
  private final Optional<Check> weather;
  private final WorldType type;
  @Getter
  private final boolean shouldStorm;

  @Getter
  private final Optional<String> path;


  public WorldModule(Match match,
      Map<GameRule, String> gamerules,
      Optional<Check> mobs,
      Optional<Difficulty> difficulty,
      Optional<Check> weather,
      WorldType type,
      Optional<String> path,
      boolean shouldStorm) {
    this.match = match;
    this.gamerules = gamerules;
    this.mobs = mobs;
    this.difficulty = difficulty;
    this.weather = weather;
    this.type = type;
    this.shouldStorm = shouldStorm;
    this.path = path;
    this.listeners = Collections.singletonList(new MobsListener(match, mobs));

    match.getFactory().getFactory(ExecutorsFactory.class)
        .registerExecutor("change-world-time", ChangeTimeExecutor::parse);
  }

  public boolean isPlaying() {
    return this.match.getRequiredModule(StatesModule.class).getState().isPlaying();
  }

  @EventHandler
  public void onMatchLoad(MatchLoadEvent event) {
    event.getCreator().type(this.type);
    event.getCreator().generator(NULL_GENERATOR);
  }

  @Override
  public void open() {
    World world = this.match.getWorld();

    // Allow mob spawning
    world.setSpawnFlags(true, true);

    // World difficulty
    if (this.difficulty.isPresent()) {
      world.setDifficulty(this.difficulty.get());
    }

    // Set xml gamerules
    for (GameRule rule : this.gamerules.keySet()) {
      world.setGameRuleValue(rule.name(), this.gamerules.get(rule));
    }

    Events.register(this.listeners);
  }

  @Override
  public void close() {
    Events.unregister(this.listeners);
  }

  @EventHandler
  public void onWeatherChange(WeatherChangeEvent event) {
    if (!shouldStorm && !event.toWeatherState()) {
      return;
    }

    if (!this.weather.isPresent()) {
      event.setCancelled(true);
      return;
    }

    CheckContext context = new CheckContext(match);
    event.setCancelled(this.weather.get().test(context).fails());
  }

  @EventHandler
  public void onThunderChange(ThunderChangeEvent event) {
    if (!this.weather.isPresent()) {
      event.setCancelled(true);
      return;
    }

    CheckContext context = new CheckContext(match);
    event.setCancelled(this.weather.get().test(context).fails());
  }

  @EventHandler
  public void onLiquidFlow(BlockFromToEvent event) {
    if (!isPlaying()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockBurn(BlockBurnEvent event) {
    if (!isPlaying()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockDispense(BlockDispenseEvent event) {
    if (!isPlaying()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockDispense(BlockDispenseEntityEvent event) {
    if (!isPlaying()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockFade(BlockFadeEvent event) {
    if (!isPlaying()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockForm(BlockFormEvent event) {
    if (!isPlaying()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockGrow(BlockGrowEvent event) {
    if (!isPlaying()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockIgnite(BlockIgniteEvent event) {
    if (!isPlaying()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockRedstone(BlockRedstoneEvent event) {
    if (!isPlaying()) {
      event.setNewCurrent(event.getOldCurrent());
    }
  }

  @EventHandler
  public void onBlockPhysics(BlockPhysicsEvent event) {
    if (!isPlaying()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onFurnaceBurn(FurnaceBurnEvent event) {
    if (!isPlaying()) {
      event.setBurning(true);
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onEntityExplode(EntityExplodeEvent event) {
    if (!isPlaying()) {
      event.setCancelled(true);
    }
  }
}
