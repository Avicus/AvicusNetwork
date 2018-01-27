package net.avicus.atlas.module.spawns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.compendium.points.AngleProvider;
import net.avicus.compendium.points.StaticAngleProvider;
import net.avicus.compendium.points.TargetPitchProvider;
import net.avicus.compendium.points.TargetYawProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@ToString
public class Spawn {

  private static final Random random = new Random();
  @Getter
  private final Optional<Group> group;
  @Getter
  private final List<SpawnRegion> regions;
  @Getter
  private final Optional<Loadout> loadout;
  @Getter
  private final Optional<Check> check;
  private final AngleProvider yaw;
  private final AngleProvider pitch;
  private final SelectionMode selectionMode;
  @Getter
  private final boolean checkAir;

  public Spawn(Optional<Group> group, List<SpawnRegion> regions, float yaw, float pitch,
      Optional<Loadout> loadout, Optional<Check> check, SelectionMode selectionMode,
      boolean checkAir) {
    this(group, regions, loadout, check, new StaticAngleProvider(yaw),
        new StaticAngleProvider(pitch), selectionMode, checkAir);
  }

  public Spawn(Optional<Group> group, List<SpawnRegion> regions, Vector look,
      Optional<Loadout> loadout, Optional<Check> check, SelectionMode selectionMode,
      boolean checkAir) {
    this(group, regions, loadout, check, new TargetYawProvider(look), new TargetPitchProvider(look),
        selectionMode, checkAir);
  }

  private Spawn(Optional<Group> group, List<SpawnRegion> regions, Optional<Loadout> loadout,
      Optional<Check> check, AngleProvider yaw, AngleProvider pitch, SelectionMode selectionMode,
      boolean checkAir) {
    this.group = group;
    this.regions = regions;
    this.loadout = loadout;
    this.check = check;
    this.yaw = yaw;
    this.pitch = pitch;
    this.selectionMode = selectionMode;
    this.checkAir = checkAir;
  }

  private static Location selectLocation(Match match, Player player, List<SpawnRegion> regions,
      SelectionMode mode, AngleProvider yaw, AngleProvider pitch, int attempts, boolean checkAir) {
    Competitor competitor = match.getRequiredModule(GroupsModule.class).getCompetitorOf(player)
        .orElse(null);

    SpawnRegion selected = null;

    if (mode == SelectionMode.RANDOM) {
      selected = regions.get(random.nextInt(regions.size()));
    } else if (mode == SelectionMode.SAFE && competitor != null) {
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
    } else {
      Map<SpawnRegion, Double> minimumDistances = new HashMap<>();

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
    }

    if (selected == null) {
      return selectLocation(match, player, regions, SelectionMode.RANDOM, yaw, pitch, attempts,
          checkAir);
    }

    Vector position = selected.randomPosition(random);

    if (checkAir) {
      AtomicInteger regionSize = new AtomicInteger();
      selected.getRegion().forEach((vector) -> regionSize.addAndGet(1));

      if (attempts <= regionSize.get()) {
        Location loc = position.toLocation(match.getWorld());
        boolean air = true;
        for (int y = loc.getBlockY(); y >= loc.getBlockY() - 10; y--) {
          if (match.getWorld().getBlockAt(new Location(match.getWorld(), loc.getX(), y, loc.getZ()))
              .getType() != Material.AIR) {
            air = false;
          }
        }

        if (air) {
          return selectLocation(match, player, regions, SelectionMode.RANDOM, yaw, pitch,
              attempts + 1, checkAir);
        }
      }
    }

    yaw = selected.getYaw().isPresent() ? selected.getYaw().get() : yaw;
    pitch = selected.getPitch().isPresent() ? selected.getPitch().get() : pitch;

    return new Location(match.getWorld(), position.getX(), position.getY(), position.getZ(),
        yaw.getAngle(position), pitch.getAngle(position));
  }

  public Location selectLocation(Match match, Player player) {
    return selectLocation(match, player, this.regions, this.selectionMode, this.yaw, this.pitch, 0,
        this.checkAir);
  }
}