package net.avicus.atlas.module.zones;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.avicus.atlas.SpecificationVersionHistory;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.InfoTable;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.SpecInformation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.EnumAttribute;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.match.registry.RegisteredObject;
import net.avicus.atlas.match.registry.WeakReference;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.executors.Executor;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.atlas.module.locales.LocalesModule;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.zones.zones.ExecutionZone;
import net.avicus.atlas.module.zones.zones.LoadoutApplicationZone;
import net.avicus.atlas.module.zones.zones.TNTCustomizationZone;
import net.avicus.atlas.module.zones.zones.TransportZone;
import net.avicus.atlas.module.zones.zones.VelocityModZone;
import net.avicus.atlas.module.zones.zones.filtered.FilteredInteractionZone;
import net.avicus.atlas.module.zones.zones.filtered.FilteredLiquidZone;
import net.avicus.atlas.module.zones.zones.filtered.FilteredMovementZone;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.atlas.util.xml.named.NamedParser;
import net.avicus.atlas.util.xml.named.NamedParsers;
import net.avicus.compendium.points.AngleProvider;
import net.avicus.compendium.points.StaticAngleProvider;
import net.avicus.compendium.points.TargetPitchProvider;
import net.avicus.compendium.points.TargetYawProvider;
import net.avicus.magma.util.region.BoundedRegion;
import net.avicus.magma.util.region.Region;
import org.bukkit.util.Vector;
import org.joda.time.Duration;

public class ZonesFactory implements ModuleFactory<ZonesModule> {

  public final static Table<Object, Method, Collection<String>> NAMED_PARSERS = HashBasedTable
      .create();

  public static final List<FeatureDocumentation> FEATURES = Lists.newArrayList();

  public ZonesFactory() {
    NAMED_PARSERS.row(this).putAll(NamedParsers.methods(ZonesFactory.class));
  }

  @Override
  public ModuleDocumentation getDocumentation() {
    ModuleDocumentation.ModuleDocumentationBuilder builder = ModuleDocumentation.builder()
        .name("Zones")
        .tagName("zones")
        .category(ModuleDocumentation.ModuleCategory.COMPONENTS)
        .description(
            "Zones are representations of locations in the match world that have special attributes and conditions that happen inside of them.");

    FeatureDocumentation.FeatureDocumentationBuilder featureDocumentationBuilder = FeatureDocumentation
        .builder()
        .name("Root Zone")
        .tagName("zone")
        .description("All zones should be wrapped inside of this tag.")
        .attribute("id", Attributes.id(true))
        .attribute("loadout", Attributes.idOf(false, "loadout"))
        .attribute("velocity", Attributes.vector(false,
            "Velocity that should be applied to the player when they enter the zone."))
        .attribute("push",
            new GenericAttribute(Double.class, true, "How far forward the player should go."))
        .attribute("icarus",
            new GenericAttribute(Double.class, true, "How high the player should go."))
        .subFeature(FeatureDocumentation.builder()
            .name("Failure Message")
            .tagName("message")
            .description(
                "This defines the message that is displayed if a player tries to do something they cannot.")
            .table(new InfoTable("Format Types", "Name", "Format").row("info", "✳ MESSAGE")
                .row("warning [default]", "⚠ MESSAGE").row("none", "MESSAGE"))
            .build())

        .subFeature(FeatureDocumentation.builder()
            .name("Checks")
            .tagName("enter")
            .tagName("leave")
            .tagName("modify")
            .tagName("place")
            .tagName("break")
            .tagName("use")
            .description("Checks can be applied to various action in the zone.")
            .description(
                "These can either be defined as IDs in the parent tag or as sub tags with the same name.")
            .subFeature(FeatureDocumentation.builder()
                .name("Enter Check")
                .tagName("enter")
                .description(
                    "This check is applied to all players before they are allowed to enter the zone.")
                .build())
            .subFeature(FeatureDocumentation.builder()
                .name("Leave Check")
                .tagName("leave")
                .description(
                    "This check is applied to all players before they are allowed to leave the zone..")
                .build())
            .subFeature(FeatureDocumentation.builder()
                .name("Modify Check")
                .tagName("modify")
                .description(
                    "This check is applied to all players before they are allowed to place/break blocks and use items in the zone.")
                .build())
            .subFeature(FeatureDocumentation.builder()
                .name("Block Place Check")
                .tagName("place")
                .description(
                    "This check is applied to all players before they are allowed to place blocks in the zone.")
                .build())
            .subFeature(FeatureDocumentation.builder()
                .name("Block Break Check")
                .tagName("break")
                .description(
                    "This check is applied to all players before they are allowed to break blocks in the zone")
                .build())
            .subFeature(FeatureDocumentation.builder()
                .name("Use Check")
                .tagName("use")
                .description(
                    "This check is applied to all players before they are allowed to use items (such as buckets) in the zone.")
                .build())
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Liquid Rules")
            .tagFilter(
                e -> e.getAttribute("water-rule") != null || e.getAttribute("lava-rule") != null)
            .description("Liquid rules are used to modify how liquids behave in this zone.")
            .subFeature(FeatureDocumentation.builder()
                .name("Water Rule")
                .tagFilter(e -> e.getAttribute("water-rule") != null)
                .description("This rule is used to change how water flows")
                .attribute("water-rule",
                    new EnumAttribute(FilteredLiquidZone.LiquidRule.class, true,
                        "The liquid rule that should be used."))
                .build())
            .subFeature(FeatureDocumentation.builder()
                .name("Lava Rule")
                .tagFilter(e -> e.getAttribute("lava-rule") != null)
                .description("This rule is used to change how lava flows")
                .attribute("lava-rule", new EnumAttribute(FilteredLiquidZone.LiquidRule.class, true,
                    "The liquid rule that should be used."))
                .build())
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Portals")
            .tagName("portal")
            .description("Portals teleport players to locations on the map when they are entered.")
            .attribute("destination", Attributes.region(true, "The destination of the portal."))
            .attribute("check",
                Attributes.check(false, "to determine if the player should be teleported"))
            .attribute("sound", new GenericAttribute(Boolean.class, false,
                "If the enderman teleport sound should be played on teleport."), true)
            .attribute("reset-velocity", new GenericAttribute(Boolean.class, false,
                "If the player's velocity should be reset when they are teleported."), true)
            .attribute("heal", new GenericAttribute(Boolean.class, false,
                "If the player should be healed to max health after they are teleported."), false)
            .attribute("feed", new GenericAttribute(Boolean.class, false,
                "If the player should be fed after they are teleported."), false)
            .attribute("look", Attributes.vector(false,
                "Location the player should be facing when they leave the portal."))
            .attribute("yaw", new GenericAttribute(Double.class, false,
                "Yaw of the player when they are teleported."))
            .attribute("pitch", new GenericAttribute(Double.class, false,
                "Pitch of the player when they are teleported."))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("TNT Customization")
            .tagName("tnt")
            .specInformation(
                SpecInformation.builder().added(SpecificationVersionHistory.REPAIR_CHECK_DEF_OFF)
                    .build())
            .description(
                "These elements allow various TNT mechanics to be customized within the zone.")
            .subFeature(FeatureDocumentation.builder()
                .name("TNT Yield")
                .tagName("yield")
                .description("The amount of items dropped by the explosion as a percentage")
                .text(new GenericAttribute(Double.class, true, "Yield of the TNT"), 0.25)
                .build())
            .subFeature(FeatureDocumentation.builder()
                .name("Instant Ignite")
                .tagName("instant-ignite")
                .description("If tha when TNT is placed, that it should instantly become primed.")
                .text(new GenericAttribute(Boolean.class, true, "If instant ignite is enabled"),
                    false)
                .build())
            .subFeature(FeatureDocumentation.builder()
                .name("TNT Power")
                .tagName("power")
                .description("Modify the explosion's block radius.")
                .text(new GenericAttribute(Double.class, true, "explosion's block radius"),
                    "The default minecraft logic")
                .build())
            .subFeature(FeatureDocumentation.builder()
                .name("TNT Fuse")
                .tagName("fuse")
                .description("Time until the TNT explodes once it is ignited.")
                .text(Attributes.duration(true, true, "fuse"), "10s")
                .build())
            .subFeature(FeatureDocumentation.builder()
                .name("Dispenser Properties")
                .tagName("dispenser")
                .description("Options for dispensers when they are blown up.")
                .attribute("nuke-limit", new GenericAttribute(Integer.class, true,
                    "Max amount of TNT a dispenser ignites when blown up."))
                .attribute("nuke-multiplier", new GenericAttribute(Number.class, true,
                    "Multiplier for the amount of TNT ignited when a dispenser is blown up."))
                .build())
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Executor Triggers")
            .tagName("triggers")
            .specInformation(
                SpecInformation.builder().added(SpecificationVersionHistory.LOADOUT_SUB_TAG)
                    .build())
            .description(
                "This can be used to run specific executors based on actions that happen in relation to the zone.")
            .attribute("enter", Attributes.idOf(false, "executor",
                "Runs when a player enters the zone region. Checks can use anything related to players."))
            .attribute("exit", Attributes.idOf(false, "executor",
                "Runs when a player exits the zone region. Checks can use anything related to players."))
            .attribute("break", Attributes.idOf(false, "executor",
                "Runs when a block is broken in the zone region. If a player broke a block, checks can use it. If an entity broke the block, checks have access to the entity. Checks also have access to the material of the block broken"))
            .attribute("place", Attributes.idOf(false, "executor",
                "Runs when a block is placed in the zone region. Checks can use anything related to players and materials."))
            .attribute("use", Attributes.idOf(false, "executor",
                "Runs when a bucket is used in the zone region. Checks can use anything related to players and materials."))
            .attribute("modify", Attributes.idOf(false, "executor",
                "Runs when a block is modified in the zone region. Checks can use the same things as with the break/place triggers."))
            .build());

    FEATURES.forEach(featureDocumentationBuilder::subFeature);

    builder.feature(featureDocumentationBuilder.build());

    return builder.build();
  }

  @Override
  public Optional<ZonesModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<XmlElement> elements = root.getChildren("zones");

    if (elements.isEmpty()) {
      return Optional.empty();
    }

    List<Zone> zones = new ArrayList<>();

    for (XmlElement element : elements) {
      for (XmlElement child : element.getDescendants()) {
        child.inheritAttributes("zones");

        if (!child.getName().equals("zone")) {
          continue;
        }

        Zone zone = parseZone(match, child);
        zones.add(zone);

        if (!zone.isActive()) {
          throw new XmlException(child, "No valid checks for zone.");
        }

        // Register ID
        if (child.hasAttribute("id")) {
          RegisteredObject registered = new RegisteredObject<>(
              child.getAttribute("id").asRequiredString(), zone);
          match.getRegistry().add(registered);
        }
      }
    }

    return Optional.of(new ZonesModule(match, zones));
  }

  private Zone parseZone(Match match, XmlElement element) {
    Region region = FactoryUtils
        .resolveRequiredRegionAs(match, Region.class, element.getAttribute("region"),
            element.getChild("region"));

    List<Zone> res = new ArrayList<>();

    // Error message
    Optional<ZoneMessage> messageMaybe = Optional.empty();
    if (element.hasChild("message")) {
      String textRaw = element.getChild("message").get().getText().asRequiredString();
      LocalizedXmlString text = match.getRequiredModule(LocalesModule.class).parse(textRaw);
      ZoneMessageFormat format = element.getChild("message").get().getAttribute("format")
          .asEnum(ZoneMessageFormat.class, true).orElse(ZoneMessageFormat.WARNING);
      messageMaybe = Optional.of(new ZoneMessage(text, format));
    } else if (element.hasAttribute("message")) {
      String textRaw = element.getAttribute("message").asRequiredString();
      LocalizedXmlString text = match.getRequiredModule(LocalesModule.class).parse(textRaw);
      ZoneMessageFormat format = element.getAttribute("format")
          .asEnum(ZoneMessageFormat.class, true).orElse(ZoneMessageFormat.WARNING);
      messageMaybe = Optional.of(new ZoneMessage(text, format));
    }

    final Optional<ZoneMessage> message = messageMaybe;

    // Checks
    Optional<Check> enter = FactoryUtils
        .resolveCheckChild(match, element.getAttribute("enter"), element.getChild("enter"));
    Optional<Check> leave = FactoryUtils
        .resolveCheckChild(match, element.getAttribute("leave"), element.getChild("leave"));
    if (enter.isPresent() || leave.isPresent()) {
      res.add(new FilteredMovementZone(match, region, message, enter, leave));
    }

    Optional<Check> modify = FactoryUtils
        .resolveCheckChild(match, element.getAttribute("modify"), element.getChild("modify"));
    Optional<Check> blockPlace = FactoryUtils
        .resolveCheckChild(match, element.getAttribute("place"), element.getChild("place"));
    Optional<Check> blockBreak = FactoryUtils
        .resolveCheckChild(match, element.getAttribute("break"), element.getChild("break"));
    Optional<Check> use = FactoryUtils
        .resolveCheckChild(match, element.getAttribute("use"), element.getChild("use"));
    if (modify.isPresent() || blockPlace.isPresent() || blockBreak.isPresent() || use
        .isPresent()) {
      res.add(
          new FilteredInteractionZone(match, region, message, modify, blockPlace,
              blockBreak, use));
    }

    Optional<FilteredLiquidZone.LiquidRule> waterRule = element.getAttribute("water-rule")
        .asEnum(FilteredLiquidZone.LiquidRule.class, true);
    Optional<FilteredLiquidZone.LiquidRule> lavaRule = element.getAttribute("lava-rule")
        .asEnum(FilteredLiquidZone.LiquidRule.class, true);
    if (waterRule.isPresent() || lavaRule.isPresent()) {
      res.add(new FilteredLiquidZone(match, region, message, waterRule, lavaRule));
    }

    NAMED_PARSERS.cellSet().forEach((c) -> {
      for (String matcher : c.getValue()) {
        if (element.hasChild(matcher)) {
          try {
            res.add((Zone) c.getColumnKey()
                .invoke(c.getRowKey(), match, element,
                    element.getRequiredChild(matcher),
                    region, message));
          } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
          }
        }
      }
    });

    // Loadout
    Optional<Loadout> loadout = FactoryUtils
        .resolveLoadout(match, element.getAttribute("loadout"), element.getChild("loadout"));
    loadout.ifPresent(l -> res.add(new LoadoutApplicationZone(match, region, message, l)));

    // Velocity
    Optional<Vector> velocity = element.getAttribute("velocity").asVector();
    Optional<Double> push = element.getAttribute("push").asDouble();
    Optional<Double> icarus = element.getAttribute("icarus").asDouble();

    if (velocity.isPresent() || push.isPresent() || icarus.isPresent()) {
      res.add(new VelocityModZone(match, region, message, velocity, push, icarus));
    }

    if (res.isEmpty()) {
      throw new XmlException(element, "Zone has no valid actors.");
    } else if (res.size() == 1) {
      return res.get(0);
    } else {
      return new ZoneNode(match, region, message, res);
    }
  }

  @NamedParser("portal")
  public TransportZone parsePortal(Match match, XmlElement element, XmlElement child, Region region,
      Optional<ZoneMessage> message) {
    BoundedRegion destination = FactoryUtils
        .resolveRequiredRegionChild(match, BoundedRegion.class, child.getAttribute("destination"),
            child.getChild("destination"));
    Optional<Check> check = FactoryUtils
        .resolveCheckChild(match, child.getAttribute("check"), child.getChild("check"));
    boolean sound = child.getAttribute("sound").asBoolean().orElse(true);
    boolean resetVelocity = child.getAttribute("reset-velocity").asBoolean().orElse(true);

    boolean heal = child.getAttribute("heal").asBoolean().orElse(true);
    boolean feed = child.getAttribute("feed").asBoolean().orElse(true);

    Optional<AngleProvider> yaw = Optional.empty();
    Optional<AngleProvider> pitch = Optional.empty();

    if (child.hasAttribute("look")) {
      Vector look = child.getAttribute("look").asRequiredVector();
      yaw = Optional.of(new TargetYawProvider(look));
      pitch = Optional.of(new TargetPitchProvider(look));
    }

    if (child.hasAttribute("yaw")) {
      yaw = Optional
          .of(new StaticAngleProvider(
              child.getAttribute("yaw").asRequiredNumber().floatValue()));
    }

    if (child.hasAttribute("pitch")) {
      pitch = Optional
          .of(new StaticAngleProvider(
              child.getAttribute("pitch").asRequiredNumber().floatValue()));
    }

    return new TransportZone(match, region, message, destination, check, sound, resetVelocity, heal,
        feed, yaw, pitch);
  }

  @NamedParser("tnt")
  public TNTCustomizationZone parseTNT(Match match, XmlElement root, XmlElement element,
      Region region, Optional<ZoneMessage> message) {
    Float yield = null;
    if (element.hasChild("yield")) {
      yield = element.getRequiredChild("yield").getText().asRequiredFlot();
    }

    Float power = null;
    if (element.hasChild("power")) {
      power = element.getRequiredChild("power").getText().asRequiredFlot();
    }

    boolean instant = element.hasChild("instant-ignite");

    Duration fuse = null;
    if (element.hasChild("fuse")) {
      fuse = element.getRequiredChild("fuse").getText().asRequiredDuration();
    }

    int nukeLimit = 16;
    Float nukeMultiplier = 0.25f;

    if (element.hasChild("dispenser")) {
      XmlElement dispenser = element.getRequiredChild("dispenser");
      nukeLimit = dispenser.getAttribute("nuke-limit").asInteger().orElse(nukeLimit);
      nukeMultiplier = dispenser.getAttribute("nuke-multiplier").asFloat().orElse(nukeMultiplier);
    }

    return new TNTCustomizationZone(match, region, message, yield, power, instant, fuse, nukeLimit,
        nukeMultiplier);
  }

  @NamedParser("triggers")
  public ExecutionZone parseTriggers(Match match, XmlElement root, XmlElement element,
      Region region, Optional<ZoneMessage> message) {
    Optional<WeakReference<Executor>> enterExecutor = Optional.empty();
    Optional<WeakReference<Executor>> exitExecutor = Optional.empty();
    Optional<WeakReference<Executor>> breakExecutor = Optional.empty();
    Optional<WeakReference<Executor>> placeExecutor = Optional.empty();
    Optional<WeakReference<Executor>> useExecutor = Optional.empty();
    Optional<WeakReference<Executor>> modifyExecutor = Optional.empty();

    if (element.hasAttribute("enter")) {
      enterExecutor = Optional.of(match.getRegistry()
          .getReference(Executor.class, element.getAttribute("enter").asRequiredString()));
    }
    if (element.hasAttribute("exit")) {
      exitExecutor = Optional.of(match.getRegistry()
          .getReference(Executor.class, element.getAttribute("exit").asRequiredString()));
    }
    if (element.hasAttribute("break")) {
      breakExecutor = Optional.of(match.getRegistry()
          .getReference(Executor.class, element.getAttribute("break").asRequiredString()));
    }
    if (element.hasAttribute("place")) {
      placeExecutor = Optional.of(match.getRegistry()
          .getReference(Executor.class, element.getAttribute("place").asRequiredString()));
    }
    if (element.hasAttribute("use")) {
      useExecutor = Optional.of(match.getRegistry()
          .getReference(Executor.class, element.getAttribute("use").asRequiredString()));
    }
    if (element.hasAttribute("modify")) {
      modifyExecutor = Optional.of(match.getRegistry()
          .getReference(Executor.class, element.getAttribute("modify").asRequiredString()));
    }

    return new ExecutionZone(match, region, message, enterExecutor, exitExecutor, breakExecutor,
        placeExecutor, useExecutor, modifyExecutor);
  }

}
