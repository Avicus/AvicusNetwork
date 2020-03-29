package net.avicus.atlas.module.checks;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.avicus.atlas.SpecificationVersionHistory;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.SpecInformation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.EnumAttribute;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.match.registry.RegisteredObject;
import net.avicus.atlas.match.registry.StaticReference;
import net.avicus.atlas.match.registry.WeakReference;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.ModuleFactorySort;
import net.avicus.atlas.module.checks.modifiers.AllCheck;
import net.avicus.atlas.module.checks.modifiers.AllowCheck;
import net.avicus.atlas.module.checks.modifiers.AnyCheck;
import net.avicus.atlas.module.checks.modifiers.DenyCheck;
import net.avicus.atlas.module.checks.modifiers.NotCheck;
import net.avicus.atlas.module.checks.types.AttackerCheck;
import net.avicus.atlas.module.checks.types.CarryingCheck;
import net.avicus.atlas.module.checks.types.DamageCheck;
import net.avicus.atlas.module.checks.types.EntityTypeCheck;
import net.avicus.atlas.module.checks.types.FlyingCheck;
import net.avicus.atlas.module.checks.types.HoldingCheck;
import net.avicus.atlas.module.checks.types.InsideCheck;
import net.avicus.atlas.module.checks.types.ItemCheck;
import net.avicus.atlas.module.checks.types.KillStreakCheck;
import net.avicus.atlas.module.checks.types.KitCheck;
import net.avicus.atlas.module.checks.types.LambdaCheck;
import net.avicus.atlas.module.checks.types.MaterialCheck;
import net.avicus.atlas.module.checks.types.ObjectiveCheck;
import net.avicus.atlas.module.checks.types.OnGroundCheck;
import net.avicus.atlas.module.checks.types.RandomCheck;
import net.avicus.atlas.module.checks.types.ScoreCheck;
import net.avicus.atlas.module.checks.types.SneakingCheck;
import net.avicus.atlas.module.checks.types.SometimesCheck;
import net.avicus.atlas.module.checks.types.SpawnCheck;
import net.avicus.atlas.module.checks.types.SprintingCheck;
import net.avicus.atlas.module.checks.types.StateCheck;
import net.avicus.atlas.module.checks.types.TeamCheck;
import net.avicus.atlas.module.checks.types.TimeCheck;
import net.avicus.atlas.module.checks.types.VictimCheck;
import net.avicus.atlas.module.checks.types.VoidCheck;
import net.avicus.atlas.module.checks.types.WearingCheck;
import net.avicus.atlas.module.checks.types.WeatherCheck;
import net.avicus.atlas.module.checks.variable.EntityVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.elimination.EliminationModule;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.kits.Kit;
import net.avicus.atlas.module.kits.KitsFactory;
import net.avicus.atlas.module.loadouts.LoadoutsFactory;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.objectives.ObjectivesFactory;
import net.avicus.atlas.module.regions.RegionsFactory;
import net.avicus.atlas.module.states.State;
import net.avicus.atlas.util.xml.XmlAttribute;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.atlas.util.xml.named.NamedParser;
import net.avicus.atlas.util.xml.named.NamedParsers;
import net.avicus.compendium.inventory.MultiMaterialMatcher;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import net.avicus.compendium.number.NumberComparator;
import net.avicus.magma.util.Version;
import net.avicus.magma.util.region.Region;
import org.apache.commons.lang.math.Range;
import org.bukkit.WeatherType;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.joda.time.Duration;

/**
 * Factory that will parse checks in XML anc add them to the match's {@link
 * net.avicus.atlas.match.registry.MatchRegistry}
 */
// We build filters first so they can be reference everywhere else.
// We must use WeakReference's here because of this.
@ModuleFactorySort(ModuleFactorySort.Order.FIRST)
public class ChecksFactory implements ModuleFactory<Module> {

  public final static Table<Object, Method, Collection<String>> NAMED_PARSERS = HashBasedTable
      .create();
  public final static Set<FeatureDocumentation> FEATURES = Sets.newHashSet();

  static {
    FEATURES.add(FeatureDocumentation.builder()
        .name("Always Check")
        .tagName("always")
        .description("This check will always pass.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Never Check")
        .tagName("never")
        .description("This check will never pass.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("All Check")
        .tagName("all")
        .description("This check will only pass if all of it's children checks pass.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Allow Check")
        .tagName("allow")
        .description("This check will only pass if the child check passes.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Any Check")
        .tagName("any")
        .description("This check will pass if any of the child checks pass.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Deny Check")
        .tagName("deny")
        .description("This check will only pass if the child check fails.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder().name("Not Check")
        .tagName("not")
        .description("This check will invert the result of the child check.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder().name("Attacker Check")
        .tagName("attacker")
        .description(
            "This check is a parent check that can be used to query any information about the attacker of a damage event.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Carrying Check")
        .tagName("carrying")
        .description(
            "This check can be used to query information about what an entity has in their inventory.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Damage Check")
        .tagName("damage")
        .description(
            "This check can be used to query information about the cause of a damage event.")
        .text(Attributes.javaDoc(true, DamageCause.class, "Damage cause to look for."))
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Entity Check")
        .tagName("entity")
        .description(
            "This check can be used to query information about the type of entity involved in an event.")
        .text(Attributes.javaDoc(true, EntityType.class, "The type of entity to check for."))
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Holding Check")
        .tagName("holding")
        .description(
            "This check is used to query information about what an entity is holding in hand.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Inside Check")
        .tagName("inside")
        .description("This check is used to check if a location is inside of a region.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Item Check")
        .tagName("item")
        .description("This check is used to query information about the type of an item.")
        .text(Attributes.materialMatcher(true, false, "The material to check against."))
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Kit Check")
        .tagName("kit")
        .description("This check is used to check which kit a player is using.")
        .specInformation(
            SpecInformation.builder().added(SpecificationVersionHistory.KIT_IDS_REQUIRED).build())
        .requirement(KitsFactory.class)
        .text(Attributes.idOf(true, "kit"))
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Material Check")
        .tagName("material")
        .description(
            "This check is used to query information about what type of material an item or block is.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Kill Streak Check")
        .tagName("kill-streak")
        .description(
            "This check is used to query information about the amount of kills a player has gotten in a row.")
        .attribute("scope", new EnumAttribute(KillStreakCheck.Scope.class, true,
            "Scope to use when checking streak."))
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Explosion Check")
        .tagName("explosion")
        .description("This check is used to check if an entity is a explosive.")
        .specInformation(
            SpecInformation.builder().added(SpecificationVersionHistory.LOADOUT_SUB_TAG).build())
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Elimination Check")
        .tagName("elimination")
        .specInformation(
            SpecInformation.builder().added(SpecificationVersionHistory.LOADOUT_SUB_TAG).build())
        .description("This check is used to check if elimination is currently enabled.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Spectating Check")
        .tagName("spectating")
        .specInformation(
            SpecInformation.builder().added(SpecificationVersionHistory.LOADOUT_SUB_TAG).build())
        .description("This check is used to check if a player is currently spectating the match.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Participating Check")
        .tagName("participating")
        .specInformation(
            SpecInformation.builder().added(SpecificationVersionHistory.LOADOUT_SUB_TAG).build())
        .description(
            "This check is used to check if a player is currently participating in the match.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Objective Check")
        .tagName("objective")
        .description(
            "This check is used to query information about the current state of an objective.")
        .attribute("team", Attributes.idOf(true, "team"))
        .attribute("state",
            new EnumAttribute(ObjectiveCheck.CheckType.class, false, "The score to check for."))
        .attribute("id", Attributes.id(false))
        .requirement(ObjectivesFactory.class)
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Random Check")
        .tagName("random")
        .description("This check will randomly return true based on chance.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Sometimes Check")
        .tagName("sometimes")
        .description("This is a random check with a 50% value.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Spawn Reason Check")
        .tagName("spawn")
        .tagName("spawn-reason")
        .description("This check is used to check the spawn reason of a spawn event.")
        .text(Attributes.javaDoc(true, SpawnReason.class, "Reason to check for."))
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("State Check")
        .tagName("state")
        .description("This check is used to check the current state of the match.")
        .description("Default states are starting, playing, and cycling")
        .text(Attributes.idOf(true, "state"))
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Team Check")
        .tagName("team")
        .description("This check is used to check what team a player is on.")
        .text(Attributes.idOf(true, "team"))
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Score Check")
        .tagName("score")
        .description("This check is used to check the current score of a team.")
        .description("If no team is specified, the team will be determined contextually.")
        .attribute("team", Attributes.idOf(true, "team"))
        .attribute("compare", Attributes.comparator(true, "the supplied value", "the score"),
            "equals")
        .text(new GenericAttribute(String.class, true, "The score to compare against."))
        .attribute("id", Attributes.id(false))
        .requirement(ObjectivesFactory.class)
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Time Check")
        .tagName("time")
        .description("This check is used to check time that the match has been playing.")
        .attribute("compare", Attributes.comparator(true, "the supplied value", "the playing time"),
            "equals")
        .text(new GenericAttribute(Integer.class, true, "The time to check against."))
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Victim Check")
        .tagName("victim")
        .description(
            "This check is a parent check that can be used to query any information about the victim of a damage event.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Void Check")
        .tagName("void")
        .specInformation(
            SpecInformation.builder().added(SpecificationVersionHistory.LOADOUT_SUB_TAG).build())
        .description("This check is used to check if a location is above the void.")
        .attribute("min",
            new GenericAttribute(Number.class, false, "The minimum y coordinate to check."), 0)
        .attribute("max",
            new GenericAttribute(Number.class, false, "The maximum y coordinate to check."), 5)
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Wearing Check")
        .tagName("wearing")
        .description("This check can be used to query information about what an entity is wearing.")
        .attribute("id", Attributes.id(false))
        .build());
    FEATURES.add(FeatureDocumentation.builder()
        .name("Weather Check")
        .tagName("weather")
        .description(
            "This check can be used to query information about what an entity has in their inventory.")
        .text(new EnumAttribute(WeatherType.class, true, "The type of weather to check for."))
        .attribute("id", Attributes.id(false))
        .build());
  }

  public ChecksFactory() {
    NAMED_PARSERS.row(this).putAll(NamedParsers.methods(ChecksFactory.class));
  }

  @Override
  public Optional<Module> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<XmlElement> elements = root.getChildren("checks");

    // built in checks
    match.getRegistry()
        .add(new RegisteredObject<>("always", new StaticResultCheck(CheckResult.ALLOW)));
    match.getRegistry().add(new RegisteredObject<>("sometimes", new SometimesCheck()));
    match.getRegistry()
        .add(new RegisteredObject<>("never", new StaticResultCheck(CheckResult.DENY)));

    if (elements.isEmpty()) {
      return Optional.empty();
    }

    elements.forEach(element -> {
      for (XmlElement child : element.getChildren()) {
        String id = child.getAttribute("id").asRequiredString();
        Check check = parseCheck(match, child);

        match.getRegistry().add(new RegisteredObject<>(id, check));
      }
    });

    return Optional.empty();
  }

  /**
   * Parse a check from an XML element.
   *
   * @param match match that the checks will be used in
   * @param element element that should be parsed
   * @return a parsed check
   */
  public Check parseCheck(Match match, XmlElement element) {
    return NamedParsers.invokeMethod(NAMED_PARSERS, element, "Invalid check type specified.",
        new Object[]{match, element, element.getChildren()});
  }

  @NamedParser("always")
  private Check parseAlways(Match match, XmlElement element, List<XmlElement> children) {
    return new StaticResultCheck(CheckResult.ALLOW);
  }

  @NamedParser("never")
  private Check parseNever(Match match, XmlElement element, List<XmlElement> children) {
    return new StaticResultCheck(CheckResult.DENY);
  }

  @NamedParser("all")
  private Check parseAll(Match match, XmlElement element, List<XmlElement> children) {
    return new AllCheck(this.parseChecks(match, element, children));
  }

  @NamedParser("allow")
  private Check parseAllow(Match match, XmlElement element, List<XmlElement> children) {
    return new AllowCheck(this.parseChecksSingleChild(match, element, children));
  }

  @NamedParser("any")
  private Check parseAny(Match match, XmlElement element, List<XmlElement> children) {
    return new AnyCheck(this.parseChecks(match, element, children));
  }

  @NamedParser("deny")
  private Check parseDeny(Match match, XmlElement element, List<XmlElement> children) {
    return new DenyCheck(this.parseChecksSingleChild(match, element, children));
  }

  @NamedParser("not")
  private Check parseNot(Match match, XmlElement element, List<XmlElement> children) {
    return new NotCheck(this.parseChecksSingleChild(match, element, children));
  }

  /**
   * Parse a collection of checks within an element.
   *
   * @param match match that the checks will be used in
   * @param parent parent that holds the checks (for the exception)
   * @param children list of elements that are checks
   * @return a list of parsed checks
   */
  public List<Check> parseChecks(Match match, XmlElement parent, List<XmlElement> children) {
    if (children.size() == 0) {
      throw new XmlException(parent, "At least one check must be specified.");
    }

    List<Check> list = children.stream()
        .map(child -> parseCheck(match, child))
        .collect(Collectors.toList());
    return list;
  }

  /**
   * Parse a check where having only one child tag is a requirement.
   *
   * @param match match that the checks will be used in
   * @param parent parent that holds the checks (for the exception)
   * @param children list of elements that are checks
   * @return the parsed check
   */
  public Check parseChecksSingleChild(Match match, XmlElement parent, List<XmlElement> children) {
    if (children.size() > 1) {
      throw new XmlException(parent, "Only one child check is allowed for the element.");
    }
    List<Check> checks = parseChecks(match, parent, children);
    return checks.get(0);
  }

  /**
   * Parse an attacker check from an XML element.
   *
   * @param match match that the checks will be used in
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("attacker")
  private AttackerCheck parseAttacker(Match match, XmlElement element, List<XmlElement> children) {
    return new AttackerCheck(parseChecksSingleChild(match, element, element.getChildren()));
  }

  /**
   * Parse a carrying check.
   *
   * @param match match that the checks will be used in
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("carrying")
  private CarryingCheck parseCarrying(Match match, XmlElement element, List<XmlElement> children) {
    LoadoutsFactory factory = match.getFactory().getFactory(LoadoutsFactory.class);
    return new CarryingCheck(factory.parseItemStack(match, element));
  }

  /**
   * Parse a damage check.
   *
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("damage")
  private DamageCheck parseDamage(Match match, XmlElement element, List<XmlElement> children) {
    DamageCause cause = element.getText().asRequiredEnum(DamageCause.class, true);
    return new DamageCheck(cause);
  }

  /**
   * Parse an entity effect check.
   *
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("entity")
  private EntityTypeCheck parseEntityType(Match match, XmlElement element,
      List<XmlElement> children) {
    EntityType type = element.getText().asRequiredEnum(EntityType.class, true);
    return new EntityTypeCheck(type);
  }

  /**
   * Parse a flying check.
   *
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("flying")
  private FlyingCheck parseFlying(Match match, XmlElement element, List<XmlElement> children) {
    return new FlyingCheck(element.getAttribute("state").asRequiredBoolean());
  }

  /**
   * Parse a holding check.
   *
   * @param match match that the checks will be used in
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("holding")
  private HoldingCheck parseHolding(Match match, XmlElement element, List<XmlElement> children) {
    LoadoutsFactory factory = match.getFactory().getFactory(LoadoutsFactory.class);
    return new HoldingCheck(factory.parseItemStack(match, element));
  }

  /**
   * Parse an inside check.
   *
   * @param match match that the checks will be used in
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("inside")
  private InsideCheck parseInside(Match match, XmlElement element, List<XmlElement> children) {
    Optional<XmlElement> inlineRegion = element.getChild("region");
    XmlAttribute regionAttribute = element.getAttribute("region");

    Optional<WeakReference<Region>> region = Optional.empty();

    if (inlineRegion.isPresent()) {
      region = Optional
          .of(new StaticReference<>(match.getFactory().getFactory(RegionsFactory.class)
              .parseRegionAs(match, inlineRegion.get(), Region.class)));
    } else if (regionAttribute.isValuePresent()) {
      region = Optional
          .of(match.getRegistry()
              .getReference(Region.class, regionAttribute.asRequiredString()));
    }

    if (!region.isPresent()) {
      throw new XmlException(element, "No region provided.");
    }

    return new InsideCheck(region);
  }

  /**
   * Parse an item check.
   *
   * @param match match that the checks will be used in
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("item")
  private ItemCheck parseItemCheck(Match match, XmlElement element, List<XmlElement> children) {
    LoadoutsFactory factory = match.getFactory().getFactory(LoadoutsFactory.class);
    return new ItemCheck(factory.parseItemStack(match, element));
  }

  /**
   * Parse a kit check.
   *
   * @param match match that the checks will be used in
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("kit")
  private KitCheck parseKit(Match match, XmlElement element, List<XmlElement> children) {
    Version needed = SpecificationVersionHistory.KIT_IDS_REQUIRED;
    if (!match.getMap().getSpecification().greaterEqual(needed)) {
      throw new XmlException(element,
          "Using this type requires specification " + needed.toString() + " or later.");
    }

    WeakReference<Kit> kit = match.getRegistry()
        .getReference(Kit.class, element.getText().asRequiredString());
    return new KitCheck(kit);
  }

  /**
   * Parse a material check.
   *
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("material")
  private MaterialCheck parseMaterial(Match match, XmlElement element, List<XmlElement> children) {
    SingleMaterialMatcher matcher = element.getText().asRequiredMaterialMatcher();
    return new MaterialCheck(matcher);
  }

  @NamedParser("kill-streak")
  private KillStreakCheck parseKillStreak(Match match, XmlElement element,
      List<XmlElement> children) {
    Range range = element.getText().asRequiredRange();
    KillStreakCheck.Scope scope = element.getAttribute("scope")
        .asRequiredEnum(KillStreakCheck.Scope.class, true);
    return new KillStreakCheck(range, scope);
  }

  @NamedParser("explosion")
  private LambdaCheck parseExplosion(Match match, XmlElement element, List<XmlElement> children) {
    return new LambdaCheck((c) -> {
      Optional<EntityVariable> var = c.getFirst(EntityVariable.class);
      if (!var.isPresent()) {
        return CheckResult.IGNORE;
      }

      EntityType entity = var.get().getEntity().getType();
      return CheckResult.valueOf(entity == EntityType.WITHER ||
          entity == EntityType.WITHER_SKULL ||
          entity == EntityType.ENDER_CRYSTAL ||
          entity == EntityType.CREEPER ||
          entity == EntityType.MINECART_TNT ||
          entity == EntityType.PRIMED_TNT ||
          entity == EntityType.FIREBALL ||
          entity == EntityType.SMALL_FIREBALL);
    });
  }

  @NamedParser("elimination")
  private LambdaCheck parseElimination(Match match, XmlElement element, List<XmlElement> children) {
    return new LambdaCheck(c -> CheckResult.valueOf(
        c.getMatch().hasModule(EliminationModule.class) && c.getMatch()
            .getRequiredModule(EliminationModule.class).isEnabled()));
  }

  @NamedParser("spectating")
  private LambdaCheck parseSpectating(Match match, XmlElement element, List<XmlElement> children) {
    return new LambdaCheck(c -> {
      Optional<PlayerVariable> player = c.getFirst(PlayerVariable.class);
      if (!player.isPresent()) {
        return CheckResult.IGNORE;
      }

      return CheckResult.valueOf(
          c.getMatch().getRequiredModule(GroupsModule.class).isObserving(player.get().getPlayer()));
    });
  }

  @NamedParser("participating")
  private LambdaCheck parseParticipating(Match match, XmlElement element,
      List<XmlElement> children) {
    return new LambdaCheck(c -> {
      Optional<PlayerVariable> player = c.getFirst(PlayerVariable.class);
      if (!player.isPresent()) {
        return CheckResult.IGNORE;
      }

      return CheckResult.valueOf(c.getMatch().getRequiredModule(GroupsModule.class)
          .getCompetitorOf(player.get().getPlayer()).isPresent());
    });
  }

  /**
   * Parse an objective check.
   *
   * @param match match that the checks will be used in
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("objective")
  private ObjectiveCheck parseObjective(Match match, XmlElement element,
      List<XmlElement> children) {
    String id = element.getText().asRequiredString();
    WeakReference<Objective> objective = match.getRegistry().getReference(Objective.class, id);
    Optional<WeakReference<Team>> team = Optional.empty();
    ObjectiveCheck.CheckType type = ObjectiveCheck.CheckType.COMPLETED;
    if (element.hasAttribute("team")) {
      team = Optional.of(match.getRegistry()
          .getReference(Team.class, element.getAttribute("team").asRequiredString()));
    }
    if (element.hasAttribute("state")) {
      type = element.getAttribute("state").asRequiredEnum(ObjectiveCheck.CheckType.class, true);
    }
    return new ObjectiveCheck(objective, team, type);
  }

  /**
   * Parse a onGround check.
   *
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("on-ground")
  private OnGroundCheck parseOnGroundCheck(Match match, XmlElement element,
      List<XmlElement> children) {
    return new OnGroundCheck(element.getAttribute("state").asRequiredBoolean());
  }

  /**
   * Parse a random check.
   *
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("random")
  private RandomCheck parseRandomCheck(Match match, XmlElement element, List<XmlElement> children) {
    double value = element.getText().asRequiredDouble();
    return new RandomCheck(value);
  }

  /**
   * Parse a sneaking check.
   *
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("sneaking")
  private SneakingCheck parseSneakingCheck(Match match, XmlElement element,
      List<XmlElement> children) {
    return new SneakingCheck(element.getAttribute("state").asRequiredBoolean());
  }

  @NamedParser("sometimes")
  private Check parseSometimes(Match match, XmlElement element, List<XmlElement> children) {
    return new SometimesCheck();
  }

  /**
   * Parse a spawn reason check.
   *
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser({"spawn", "spawn-reason"})
  private SpawnCheck parseSpawnReason(Match match, XmlElement element, List<XmlElement> children) {
    SpawnReason reason = element.getText().asRequiredEnum(SpawnReason.class, true);
    return new SpawnCheck(reason);
  }

  /**
   * Parse a sprinting check.
   *
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("sprinting")
  private SprintingCheck parseSprinting(Match match, XmlElement element,
      List<XmlElement> children) {
    return new SprintingCheck(element.getAttribute("state").asRequiredBoolean());
  }

  /**
   * Parse a match state check.
   *
   * @param match match that the checks will be used in
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("state")
  public StateCheck parseState(Match match, XmlElement element, List<XmlElement> children) {
    WeakReference<State> state = match.getRegistry()
        .getReference(State.class, element.getText().asRequiredString());
    return new StateCheck(state);
  }

  /**
   * Parse a team check.
   *
   * @param match match that the checks will be used in
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("team")
  public TeamCheck parseTeam(Match match, XmlElement element, List<XmlElement> children) {
    WeakReference<Team> team = match.getRegistry()
        .getReference(Team.class, element.getText().asRequiredString());
    return new TeamCheck(match, team);
  }

  /**
   * Parse a score check.
   *
   * @param match match that the checks will be used in
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("score")
  public ScoreCheck parseScore(Match match, XmlElement element, List<XmlElement> children) {
    Optional<WeakReference<Competitor>> comp = Optional.empty();
    if (element.hasAttribute("team")) {
      element.getAttribute("team").asRequiredString();
      comp = Optional.of(match.getRegistry()
          .getReference(Competitor.class, element.getText().asRequiredString()));
    }
    NumberComparator comparator = element.getAttribute("compare").asComparator()
        .orElse(NumberComparator.EQUALS);
    int value = element.getText().asRequiredInteger();

    return new ScoreCheck(value, comp, comparator);
  }

  /**
   * Parse a time check.
   *
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("time")
  public TimeCheck parseTime(Match match, XmlElement element, List<XmlElement> children) {
    Duration duration = element.getText().asRequiredDuration();
    NumberComparator comparator = element.getAttribute("compare").asComparator()
        .orElse(NumberComparator.EQUALS);
    return new TimeCheck(duration, comparator);
  }

  /**
   * Parse a victim check.
   *
   * @param match match that the checks will be used in
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("victim")
  private VictimCheck parseVictim(Match match, XmlElement element, List<XmlElement> children) {
    return new VictimCheck(parseChecksSingleChild(match, element, element.getChildren()));
  }

  @NamedParser("void")
  public VoidCheck parseVoid(Match match, XmlElement element, List<XmlElement> children) {
    int min = element.getAttribute("min").asInteger().orElse(0);
    int max = element.getAttribute("max").asInteger().orElse(5);
    Optional<MultiMaterialMatcher> ignoredBlocks = element.getAttribute("ignored-blocks")
        .asMultiMaterialMatcher();
    return new VoidCheck(min, max, ignoredBlocks);
  }

  /**
   * Parse a wearing check.
   *
   * @param match match that the checks will be used in
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("wearing")
  private WearingCheck parseWearing(Match match, XmlElement element, List<XmlElement> children) {
    LoadoutsFactory factory = match.getFactory().getFactory(LoadoutsFactory.class);
    return new WearingCheck(factory.parseItemStack(match, element));
  }

  /**
   * Parse a weather check.
   *
   * @param element element that the check is is in
   * @return the parsed check
   */
  @NamedParser("weather")
  public WeatherCheck parseWeather(Match match, XmlElement element, List<XmlElement> children) {
    WeatherType type = element.getText().asRequiredEnum(WeatherType.class, true);
    return new WeatherCheck(type);
  }

  @Override
  public ModuleDocumentation getDocumentation() {
    ModuleDocumentation.ModuleDocumentationBuilder builder = ModuleDocumentation.builder()
        .category(ModuleDocumentation.ModuleCategory.CORE)
        .tagName("checks")
        .name("Checks")
        .description(
            "Checks are conditions that must pass in order for an action to happen in a match.")
        .description("On their own, they do nothing.");

    FEATURES.forEach(builder::feature);

    for (String s : new String[]{"Flying", "On Ground", "Sneaking", "Sprinting"}) {
      builder.feature(FeatureDocumentation.builder()
          .name(s + " Check")
          .tagName(s.toLowerCase().replace(" ", "-"))
          .description("This check is used to check if a player is " + s.toLowerCase() + ".")
          .attribute("state", new GenericAttribute(Boolean.class, true))
          .attribute("id", Attributes.id(false))
          .build());
    }

    return builder.build();
  }

}
