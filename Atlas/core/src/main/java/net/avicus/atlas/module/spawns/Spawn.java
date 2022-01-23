package net.avicus.atlas.module.spawns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.atlas.runtimeconfig.RuntimeConfigurable;
import net.avicus.atlas.runtimeconfig.fields.AngleProviderField;
import net.avicus.atlas.runtimeconfig.fields.ConfigurableField;
import net.avicus.atlas.runtimeconfig.fields.EnumField;
import net.avicus.atlas.runtimeconfig.fields.SimpleFields.BooleanField;
import net.avicus.compendium.points.AngleProvider;
import net.avicus.compendium.points.StaticAngleProvider;
import net.avicus.compendium.points.TargetPitchProvider;
import net.avicus.compendium.points.TargetYawProvider;
import net.avicus.magma.channel.staff.StaffChannels;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@ToString
public class Spawn implements RuntimeConfigurable {

  private static final Random RANDOM = new Random();
  @Getter
  private final Optional<Group> group;
  @Getter
  private final List<SpawnRegion> regions;
  @Getter
  private final Optional<Loadout> loadout;
  @Getter
  private final Optional<Check> check;
  private AngleProvider yaw;
  private AngleProvider pitch;
  private SelectionMode selectionMode;
  @Getter
  private boolean ensureSafe;

  public Spawn(Optional<Group> group, List<SpawnRegion> regions, float yaw, float pitch,
      Optional<Loadout> loadout, Optional<Check> check, SelectionMode selectionMode,
      boolean ensureSafe) {
    this(group, regions, loadout, check, new StaticAngleProvider(yaw),
        new StaticAngleProvider(pitch), selectionMode, ensureSafe);
  }

  public Spawn(Optional<Group> group, List<SpawnRegion> regions, Vector look,
      Optional<Loadout> loadout, Optional<Check> check, SelectionMode selectionMode,
      boolean ensureSafe) {
    this(group, regions, loadout, check, new TargetYawProvider(look), new TargetPitchProvider(look),
        selectionMode, ensureSafe);
  }

  private Spawn(Optional<Group> group, List<SpawnRegion> regions, Optional<Loadout> loadout,
      Optional<Check> check, AngleProvider yaw, AngleProvider pitch, SelectionMode selectionMode,
      boolean ensureSafe) {
    this.group = group;
    this.regions = regions;
    this.loadout = loadout;
    this.check = check;
    this.yaw = yaw;
    this.pitch = pitch;
    this.selectionMode = selectionMode;
    this.ensureSafe = ensureSafe;
  }

  /** Select a region, spreading players out evenly regardless of relation **/
  private static SpawnRegion selectSpread(Match match, Player player, List<SpawnRegion> regions) {
    Map<SpawnRegion, Double> minimumDistances = new HashMap<>();

    SpawnRegion selected = null;

    for (Player test : match.getPlayers()) {
      if (test.equals(player)) {
        continue;
      }

      for (SpawnRegion region : regions) {
        double distance = region.getCenter().distanceSquared(test.getLocation().toVector());
        double minimumDistance = minimumDistances.getOrDefault(region, Double.MAX_VALUE);
        if (distance < minimumDistance) {
          minimumDistances.put(region, distance);
        }
      }
    }

    double mostIsolatedDistance = Double.MIN_VALUE;

    for (Entry<SpawnRegion, Double> entry : minimumDistances.entrySet()) {
      if (entry.getValue() > mostIsolatedDistance) {
        selected = entry.getKey();
        mostIsolatedDistance = entry.getValue();
      }
    }

    return selected;
  }

  /** Select a region, spreading players out so they are furthest away from enemies **/
  private static SpawnRegion selectSafe(Match match, Player player, List<SpawnRegion> regions) {
    SpawnRegion selected = null;

    Map<SpawnRegion, Double> minimumDistances = new HashMap<>();

    for (Competitor otherCompetitor : match.getRequiredModule(GroupsModule.class)
        .getCompetitors()) {
      for (Player test : otherCompetitor.getPlayers()) {
        if (test.equals(player)) {
          continue;
        }

        for (SpawnRegion region : regions) {
          double distance = region.getCenter().distanceSquared(test.getLocation().toVector());
          double minimumDistance = minimumDistances.getOrDefault(region, Double.MAX_VALUE);
          if (distance < minimumDistance) {
            minimumDistances.put(region, distance);
          }
        }
      }
    }

    double mostIsolatedDistance = Double.MIN_VALUE;

    for (Entry<SpawnRegion, Double> entry : minimumDistances.entrySet()) {
      if (entry.getValue() > mostIsolatedDistance) {
        selected = entry.getKey();
        mostIsolatedDistance = entry.getValue();
      }
    }

    return selected;
  }

  private static SpawnRegion selectRandom(List<SpawnRegion> regions) {
    return regions.get(RANDOM.nextInt(regions.size()));
  }

  /** Find a position in a region which is safe. Will return null if region has no safe places **/
  private static Vector findSafePosition(Match match, SpawnRegion region) {
    int attempts = 0;
    AtomicInteger regionSize = new AtomicInteger();
    region.getRegion().forEach(vec -> regionSize.incrementAndGet());
    Vector position = null;
    while (attempts <= regionSize.get()) {
      position = region.randomPosition(RANDOM);
      Location loc = position.toLocation(match.getWorld());
      boolean safe = false;

      if (loc.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType().isSolid()) continue;

      for (int y = loc.getBlockY(); y >= loc.getBlockY() - 10; y--) {
        if (match.getWorld().getBlockAt(new Location(match.getWorld(), loc.getX(), y, loc.getZ()))
            .getType() != Material.AIR) {
          safe = true;
          break;
        }
      }

      if (safe) break;
      position = null;
      attempts++;
    }

    return position;
  }

  /** Try to find a safe place in the pre-selected region first, then fallback to checking the rest **/
  private static Vector ensureSafe(Match match, SpawnRegion selected, List<SpawnRegion> regions, Player player) {
    Vector position = null;

    List<SpawnRegion> regionsToTry = new ArrayList<>(regions);
    regionsToTry.remove(selected);
    Collections.shuffle(regionsToTry);
    regionsToTry.add(0, selected);

    for (SpawnRegion region : regionsToTry) {
      position = findSafePosition(match, region);
      if (position != null) break;
    }

    if (position == null) {
      StaffChannels.MAPDEV_CHANNEL.simpleLocalSend(null, new TextComponent("Failed to find suitable spawn position for " + player.getName()));
      position = selectRandom(regions).randomPosition(RANDOM);
    }

    return position;
  }

  private static Location selectLocation(Match match, Player player, List<SpawnRegion> regions,
      SelectionMode mode, AngleProvider yaw, AngleProvider pitch, boolean ensureSafe) {
    Competitor competitor = match.getRequiredModule(GroupsModule.class).getCompetitorOf(player)
        .orElse(null);

    SpawnRegion selected = null;

    if (mode == SelectionMode.RANDOM) {
      selected = selectRandom(regions);
    } else if (mode == SelectionMode.SAFE && competitor != null) {
      selected = selectSafe(match, player, regions);
    } else {
      selected = selectSpread(match, player, regions);
    }

    if (selected == null) {
      selected = selectRandom(regions);
    }

    Vector position;

    if (ensureSafe) {
      position = ensureSafe(match, selected, regions, player);
    } else {
      position = selected.randomPosition(RANDOM);
    }

    yaw = selected.getYaw().isPresent() ? selected.getYaw().get() : yaw;
    pitch = selected.getPitch().isPresent() ? selected.getPitch().get() : pitch;

    return new Location(match.getWorld(), position.getX(), position.getY(), position.getZ(),
        yaw.getAngle(position), pitch.getAngle(position));
  }

  public Location selectLocation(Match match, Player player) {
    return selectLocation(match, player, this.regions, this.selectionMode, this.yaw, this.pitch,
        this.ensureSafe);
  }

  @Override
  public ConfigurableField[] getFields() {
    return new ConfigurableField[]{
        new AngleProviderField("Yaw", () -> this.yaw, (v) -> this.yaw = v),
        new AngleProviderField("Pitch", () -> this.pitch, (v) -> this.pitch = v),
        new EnumField<>("Selection Mode", () -> this.selectionMode, (v) -> this.selectionMode = v, SelectionMode.class),
        new BooleanField("Ensure Safe", () -> this.ensureSafe, (v) -> this.ensureSafe = v)
    };
  }

  @Override
  public String getDescription(CommandSender viewer) {
    return this.group.map(g -> g.getChatColor() + g.getName().translateDefault() + ChatColor.RESET + "'s ")
        .orElse("") + "Spawn";
  }

  @Override
  public List<RuntimeConfigurable> getChildren() {
    return this.regions.stream().map(r -> (RuntimeConfigurable)r).collect(Collectors.toList());
  }
}