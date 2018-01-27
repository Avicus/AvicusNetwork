package net.avicus.atlas.module.spawns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.attributes.Attribute;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.EnumAttribute;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.compendium.points.AngleProvider;
import net.avicus.compendium.points.StaticAngleProvider;
import net.avicus.compendium.points.TargetPitchProvider;
import net.avicus.compendium.points.TargetYawProvider;
import net.avicus.magma.util.region.BoundedRegion;
import org.bukkit.util.Vector;
import org.joda.time.Duration;

public class SpawnsFactory implements ModuleFactory<SpawnsModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .name("Spawns")
        .tagName("spawns")
        .category(ModuleDocumentation.ModuleCategory.CORE)
        .description(
            "Spawns are regions where players spawn into the map upon joining or re-spawning after death.")
        .feature(FeatureDocumentation.builder()
            .name("Base Configuration")
            .tagName("spawns")
            .description("These attributes contain basic configuration options for all spawns.")
            .attribute("respawn-delay",
                Attributes.duration(false, true, "The time it takes for a player to respawn."),
                "2s")
            .attribute("auto-respawn", new GenericAttribute(Boolean.class, false,
                "If players should automatically respawn after the delay has elapsed."), false)
            .attribute("respawn-freeze", new GenericAttribute(Boolean.class, false,
                "If players should be frozen in place while they are re-spawning."), true)
            .attribute("respawn-blindness", new GenericAttribute(Boolean.class, false,
                "If players should be blinded while they are re-spawning."), false)
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Spawn")
            .tagName("spawn")
            .description(
                "Spawns indicate locations where players should spawn with an option loadout.")
            .attribute("team",
                Attributes.idOf(false, "team", "Team that should spawn at this locaion."))
            .attribute("region",
                Attributes.region(false, "The region that this spawn should place players in."))
            .attribute("regions", new Attribute() {
              @Override
              public String getName() {
                return "Region Group";
              }

              @Override
              public boolean isRequired() {
                return false;
              }

              @Override
              public String[] getDescription() {
                return new String[]{
                    "A collection of regions that this spawn should place players in.",
                    "This creates a pseudo join region of all of the children."};
              }
            })
            .attribute("look", Attributes
                .vector(false, "The location the player should be looking when they spawn."))
            .attribute("pitch", new GenericAttribute(Integer.class, false,
                "The player's head pitch when they spawn."))
            .attribute("yaw", new GenericAttribute(Integer.class, false,
                "The player's head yaw when they spawn."))
            .attribute("loadout",
                Attributes.idOf(false, "loadout", "Loadout to give the player when they spawn."))
            .attribute("check", Attributes.check(false, "before a player is allowed to spawn here"))
            .attribute("mode", new EnumAttribute(SelectionMode.class, false), SelectionMode.RANDOM)
            .attribute("check-air", new GenericAttribute(Boolean.class, false,
                    "If each location of the region should be checked to see if it is safe to spawn in."),
                false)
            .subFeature(FeatureDocumentation.builder()
                .name("Regions")
                .tagName("regions")
                .description(
                    "Special attributes can be added to each top-level region to change behavious of spawning in that specific area.")
                .attribute("look", Attributes.vector(false,
                    "The location the player should be looking when they spawn."))
                .attribute("pitch", new GenericAttribute(Integer.class, false,
                    "The player's head pitch when they spawn."))
                .attribute("yaw", new GenericAttribute(Integer.class, false,
                    "The player's head yaw when they spawn."))
                .build())
            .build())
        .build();
  }

  @Override
  public Optional<SpawnsModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<Spawn> spawns = new ArrayList<>();

    List<XmlElement> elements = root.getChildren("spawns");

    if (elements.isEmpty()) {
      throw new ModuleBuildException(this, "No spawns defined.");
    }

    spawns.addAll(elements.stream().flatMap(e -> e.getChildren().stream())
        .map(child -> buildSpawn(match, child))
        .collect(Collectors.toList()));

    Duration respawnDelay = Duration.millis(2000);
    boolean autoRespawn = false;
    boolean respawnFreeze = true;
    boolean respawnBlindness = false;

    for (XmlElement element : elements) {
      respawnDelay = element.getAttribute("respawn-delay").asDuration().orElse(respawnDelay);
      autoRespawn = element.getAttribute("auto-respawn").asBoolean().orElse(autoRespawn);
      respawnFreeze = element.getAttribute("respawn-freeze").asBoolean().orElse(respawnFreeze);
      respawnBlindness = element.getAttribute("respawn-blindness").asBoolean()
          .orElse(respawnBlindness);
    }

    return Optional.of(new SpawnsModule(match, spawns, respawnDelay, autoRespawn, respawnFreeze,
        respawnBlindness));
  }

  private Spawn buildSpawn(Match match, XmlElement element) {
    Optional<Group> group = Optional.empty();
    if (element.hasAttribute("team")) {
      String teamId = element.getAttribute("team").asRequiredString();
      group = match.getRegistry().get(Group.class, teamId, true);
    }

    List<SpawnRegion> regions = new ArrayList<>();

    if (element.hasAttribute("region")) {
      BoundedRegion region = FactoryUtils
          .resolveRequiredRegionAs(match, BoundedRegion.class, element.getAttribute("region"),
              Optional.empty());
      regions.add(new SpawnRegion(region, Optional.empty(), Optional.empty()));
    } else if (element.hasChild("region")) {
      BoundedRegion region = FactoryUtils
          .resolveRequiredRegionAs(match, BoundedRegion.class, element.getAttribute("region"),
              element.getChild("region"));
      regions.add(new SpawnRegion(region, Optional.empty(), Optional.empty()));
    } else if (element.hasChild("regions")) {
      for (XmlElement child : element.getChild("regions").get().getChildren()) {
        Optional<AngleProvider> yaw = Optional.empty();
        Optional<AngleProvider> pitch = Optional.empty();

        if (child.hasAttribute("look")) {
          Vector look = child.getAttribute("look").asRequiredVector();
          yaw = Optional.of(new TargetYawProvider(look));
          pitch = Optional.of(new TargetPitchProvider(look));
        }
        if (child.hasAttribute("yaw")) {
          float yawValue = child.getAttribute("yaw").asNumber().orElse(0).floatValue();
          yaw = Optional.of(new StaticAngleProvider(yawValue));
        }
        if (child.hasAttribute("pitch")) {
          float pitchValue = child.getAttribute("pitch").asNumber().orElse(0).floatValue();
          pitch = Optional.of(new StaticAngleProvider(pitchValue));
        }

        BoundedRegion region = FactoryUtils
            .resolveRequiredRegion(match, BoundedRegion.class, child.getAttribute("id"),
                Optional.of(child));
        regions.add(new SpawnRegion(region, yaw, pitch));
      }
    }

    Optional<Loadout> loadout = FactoryUtils
        .resolveLoadout(match, element.getAttribute("loadout"), element.getChild("loadout"));

    Optional<Check> check = FactoryUtils
        .resolveCheck(match, element.getAttribute("check"), element.getChild("check"));

    SelectionMode mode = element.getAttribute("mode").asEnum(SelectionMode.class, true)
        .orElse(SelectionMode.RANDOM);

    boolean checkAir = element.getAttribute("check-air").asBoolean().orElse(false);

    Spawn spawn;

    if (regions.size() == 1) {
      regions = Collections.singletonList(regions.get(0));
    }

    if (regions.isEmpty()) {
      throw new XmlException(element, "No spawn regions were defined.");
    }

    if (element.hasAttribute("look")) {
      Vector look = element.getAttribute("look").asRequiredVector();
      spawn = new Spawn(group, regions, look, loadout, check, mode, checkAir);
    } else {
      float yaw = element.getAttribute("yaw").asNumber().orElse(0).floatValue();
      float pitch = element.getAttribute("pitch").asNumber().orElse(0).floatValue();
      spawn = new Spawn(group, regions, yaw, pitch, loadout, check, mode, checkAir);
    }

    return spawn;
  }
}
