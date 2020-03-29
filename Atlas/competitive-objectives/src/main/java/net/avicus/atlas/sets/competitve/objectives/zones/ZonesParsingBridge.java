package net.avicus.atlas.sets.competitve.objectives.zones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.registry.WeakReference;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.objectives.ObjectivesFactory;
import net.avicus.atlas.module.zones.ZoneMessage;
import net.avicus.atlas.module.zones.ZonesFactory;
import net.avicus.atlas.sets.competitve.objectives.flag.FlagObjective;
import net.avicus.atlas.sets.competitve.objectives.zones.flag.NetZone;
import net.avicus.atlas.sets.competitve.objectives.zones.flag.PostZone;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.atlas.util.xml.named.NamedParser;
import net.avicus.atlas.util.xml.named.NamedParsers;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import net.avicus.compendium.number.NumberAction;
import net.avicus.magma.util.region.BoundedRegion;
import net.avicus.magma.util.region.Region;
import org.bukkit.Material;
import org.joda.time.Duration;

public class ZonesParsingBridge {

  public void buildBridge() {
    ZonesFactory.NAMED_PARSERS.row(this).putAll(NamedParsers.methods(ZonesParsingBridge.class));

    ZonesFactory.FEATURES.add(FeatureDocumentation.builder()
        .name("Flag Post")
        .tagName("post")
        .requirement(ObjectivesFactory.class)
        .description("These define where flags should be placed.")
        .attribute("yaw", new GenericAttribute(Double.class, false, "Yaw of the flag."))
        .attribute("check", Attributes.check(false, "before a player can pick up the flag"))
        .build());
    ZonesFactory.FEATURES.add(FeatureDocumentation.builder()
        .requirement(ObjectivesFactory.class)
        .name("Flag Capture Net")
        .tagName("net")
        .description(
            "Nets are locations that flags can be captured at. A flag can have multiple net possibilities, and nets do not have to be bound to teams.")
        .attribute("owner", Attributes.idOf(false, "flag owner"))
        .attribute("points", new GenericAttribute(Number.class, false,
            "Points to be rewarded to the player who captures the flag."))
        .attribute("check",
            Attributes.check(false, "to determine if the player can capture the flag at this net"))
        .attribute("respawn-together", new GenericAttribute(Boolean.class, false,
                "If all of the flags enclosed by the flags element should wait until all are captured to respawn, or respawn directly after capture."),
            false)
        .attribute("respawn-post", Attributes.idOf(false, "flag post"))
        .build());

    ZonesFactory.FEATURES.add(FeatureDocumentation.builder()
        .name("Score Box")
        .tagName("scorebox")
        .description("Score boxes are areas in which players enter to earn points.")
        .requirement(ObjectivesFactory.class)
        .attribute("points", new GenericAttribute(Integer.class, true,
            "The amount of points that this score box rewards."))
        .attribute("action", Attributes.action(false, "the player's score"), "set")
        .attribute("points-growth", new GenericAttribute(Double.class, false,
            "The multiplier used for points each time this score box is entered."))
        .attribute("check", Attributes.check(false, "before points are rewarded"))
        .subFeature(FeatureDocumentation.builder()
            .name("Item Rewards")
            .tagName("item-rewards")
            .description(
                "Item rewards allow score boxes to be configured to reward more points based on items in the player's inventory.")
            .text(Attributes
                .materialMatcher(true, false, "The type of item that this matcher applies for."))
            .attribute("points", new GenericAttribute(Integer.class, true,
                "The amount of points that should be rewarded for this item."))
            .build())
        .build());
  }

  @NamedParser("scorebox")
  public ScoreZone parseScorebox(Match match, XmlElement element, XmlElement child, Region region,
      Optional<ZoneMessage> message) {
    int points = child.getAttribute("points").asRequiredInteger();
    NumberAction action = child.getAttribute("action").asNumberAction().orElse(NumberAction.ADD);

    Optional<Double> pointsGrowth = child.getAttribute("points-growth").asDouble();
    Optional<Check> check = FactoryUtils
        .resolveCheckChild(match, element.getAttribute("check"), element.getChild("check"));

    Optional<HashMap<Material, HashMap<SingleMaterialMatcher, Integer>>> rewardables = Optional
        .empty();
    if (child.hasChild("item-rewards")) {
      HashMap<Material, HashMap<SingleMaterialMatcher, Integer>> matchersWithPoints = new HashMap<>();
      XmlElement rewardChild = child.getChild("item-rewards").get();
      for (XmlElement item : rewardChild.getChildren("item")) {
        SingleMaterialMatcher matcher = item.getText().asRequiredMaterialMatcher();
        int itemPoints = item.getAttribute("points").asRequiredInteger();
        HashMap<SingleMaterialMatcher, Integer> pointsMap = new HashMap<>();
        pointsMap.put(matcher, itemPoints);
        matchersWithPoints.put(matcher.getMaterial(), pointsMap);
      }
      rewardables = Optional.of(matchersWithPoints);
    }

    return new ScoreZone(match, region, message, points, action, pointsGrowth, check, rewardables);
  }

  @NamedParser("post")
  public PostZone parsePost(Match match, XmlElement root, XmlElement element, Region region,
      Optional<ZoneMessage> message) {
    if (!(region instanceof BoundedRegion)) {
      throw new XmlException(element, "Posts require bounded regions.");
    }

    Optional<Check> pickup = FactoryUtils
        .resolveCheckChild(match, element.getAttribute("pickup"), element.getChild("pickup"));
    float yaw = element.getAttribute("yaw").asRequiredNumber().floatValue();

    return new PostZone(match, (BoundedRegion) region, message, yaw, pickup);
  }

  @NamedParser("net")
  public NetZone parseNet(Match match, XmlElement root, XmlElement element, Region region,
      Optional<ZoneMessage> message) {
    if (!(region instanceof BoundedRegion)) {
      throw new XmlException(element, "Nets require bounded regions.");
    }

    Optional<Team> owner = Optional.empty();
    if (element.hasAttribute("owner")) {
      owner = match.getRegistry()
          .get(Team.class, element.getAttribute("owner").asRequiredString(), false);
    }
    Optional<Integer> points = element.getAttribute("points").asInteger();
    Optional<Check> captureCheck = FactoryUtils
        .resolveCheckChild(match, element.getAttribute("capture-check"),
            element.getChild("capture-check"));

    Optional<WeakReference<PostZone>> post = Optional.empty();
    if (element.hasAttribute("respawn-post")) {
      post = Optional.of(match.getRegistry()
          .getReference(PostZone.class, element.getAttribute("respawn-post").asRequiredString()));
    }

    boolean respawnTogether = element.getAttribute("respawn-together").asBoolean().orElse(false);
    Optional<List<WeakReference<FlagObjective>>> respawnFlags = Optional.empty();

    if (respawnTogether) {
      List<WeakReference<FlagObjective>> flags = new ArrayList<>();
      for (XmlElement flag : element.getChildren("flag")) {
        flags.add(match.getRegistry()
            .getReference(FlagObjective.class, flag.getText().asRequiredString()));
      }
      respawnFlags = Optional.of(flags);
    }

    Optional<Duration> respawnDelay = element.getAttribute("respawn-delay").asDuration();

    return new NetZone(match, (BoundedRegion) region, message, owner, points, post, respawnFlags,
        captureCheck, respawnTogether, respawnDelay);
  }
}
