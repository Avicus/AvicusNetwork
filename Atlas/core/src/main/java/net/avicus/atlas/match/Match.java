package net.avicus.atlas.match;

import com.google.common.base.Preconditions;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.AtlasConfig;
import net.avicus.atlas.event.match.MatchCloseEvent;
import net.avicus.atlas.event.match.MatchLoadEvent;
import net.avicus.atlas.event.match.MatchOpenEvent;
import net.avicus.atlas.map.AtlasMap;
import net.avicus.atlas.match.registry.MatchRegistry;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.world.WorldModule;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.magma.util.Version;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

@ToString
public class Match {

  @Getter
  private final AtlasMap map;
  @Getter
  private final MatchFactory factory;
  @Getter
  private final MatchRegistry registry;
  @Getter
  private final Set<Module> modules;
  @Getter
  private String id;
  @Getter
  private boolean loaded = false;

  public Match(AtlasMap map, MatchFactory factory) {
    this.id = UUID.randomUUID().toString().substring(0, 4);
    this.map = map;
    this.factory = factory;
    this.registry = new MatchRegistry(this);
    this.modules = new HashSet<>();
  }

  public Collection<Player> getPlayers() {
    // This is here to avoid using Bukkit.getOnlinePlayers elsewhere.
    // (in case multiple matches will run on one server)
    return Bukkit.getOnlinePlayers().stream().collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  public <T extends Module> Optional<T> getModule(Class<T> type) {
    for (Module module : this.modules) {
      Class test = module.getClass();
      if (type.isAssignableFrom(test)) {
        return Optional.of((T) module);
      }
    }
    return Optional.empty();
  }

  public boolean hasModule(Class<? extends Module> type) {
    return getModule(type).isPresent();
  }

  /**
   * Get a module that is required.
   */
  public <T extends Module> T getRequiredModule(Class<T> type) {
    Optional<T> module = getModule(type);
    Preconditions.checkArgument(module.isPresent(), "Required module is not present.");
    return module.get();
  }

  public void addModule(Module module) {
    this.modules.add(module);
  }

  public File getFolder() {
    return new File(getWorldName());
  }

  private String getWorldName() {
    Optional<WorldModule> module = getModule(WorldModule.class);
    if (module.isPresent() && module.get().getPath().isPresent()) {
      return "matches/match-" + this.id + "/" + module.get().getPath().get();
    }

    return "matches/match-" + this.id;
  }

  public void warn(Exception e) {
    Atlas.get().getMapErrorLogger().warning(e.getMessage());
  }

  public void warnDeprecation(String message, Version changed) {
    if (AtlasConfig.isSendDeprecationWarnings())
      Atlas.get().getMapErrorLogger().info(
          "DEPRECATION NOTICE: " + message + " Changed in: " + changed.toString() + " Current: " + map
              .getSpecification().toString());
  }

  public World getWorld() {
    return Bukkit.getWorld(getWorldName());
  }

  public void load() throws IOException {
    this.modules.forEach(Events::register);

    WorldCreator creator = new WorldCreator(getWorldName());

    MatchLoadEvent event = new MatchLoadEvent(this, creator);
    Events.call(event);

    Optional<WorldModule> module = getModule(WorldModule.class);
    if (module.isPresent() && module.get().getPath().isPresent()) {
      this.map.getSource().copyWorld(getFolder(), module.get().getPath().get());
    } else {
      this.map.getSource().copyWorld(getFolder());
    }

    World world = event.getCreator().createWorld();

    if (this.hasModule(WorldModule.class) && !this.getRequiredModule(WorldModule.class)
        .isShouldStorm() && world.hasStorm()) {
      world.setWeatherDuration(0);
      world.setStorm(false);
    }

    world.setAutoSave(false);
    this.loaded = true;
  }

  public void open() {
    this.modules.forEach(Module::open);

    MatchOpenEvent event = new MatchOpenEvent(this);
    Events.call(event);
  }

  public void close() {
    MatchCloseEvent event = new MatchCloseEvent(this);
    Events.call(event);

    for (Module module : this.modules) {
      Events.unregister(module);
      module.close();
    }
    this.loaded = false;
  }

  public void unloadWorld() {
    Bukkit.unloadWorld(getWorldName(), false);
  }

  public void deleteWorld() throws IOException {
    FileUtils.deleteDirectory(getFolder());
  }

  public void broadcast(Localizable translation) {
    for (Player player : this.getPlayers()) {
      player.sendMessage(translation);
    }

    Bukkit.getConsoleSender().sendMessage(translation);
  }

  public void importantBroadcast(Localizable translation) {
    this.broadcast(Messages.UI_IMPORTANT.with(TextStyle.ofBold(), translation));
  }
}
