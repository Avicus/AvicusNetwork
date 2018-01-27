package net.avicus.atlas.module.executors.types;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import lombok.ToString;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.executors.Executor;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.compendium.points.AngleProvider;
import net.avicus.compendium.points.StaticAngleProvider;
import net.avicus.magma.util.region.BoundedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * An executor that teleports a player to a location inside of a region.
 */
@ToString
public class TeleportExecutor extends Executor {

  private static final Random random = new Random();

  private final BoundedRegion region;
  private final Optional<AngleProvider> yaw;
  private final Optional<AngleProvider> pitch;

  public TeleportExecutor(String id, Check check, BoundedRegion region, Optional<AngleProvider> yaw,
      Optional<AngleProvider> pitch) {
    super(id, check);
    this.region = region;
    this.yaw = yaw;
    this.pitch = pitch;
  }

  public static FeatureDocumentation documentation() {
    return FeatureDocumentation.builder()
        .name("Teleport Player")
        .tagName("teleport-player")
        .description("An executor that teleports a player to a location inside of a region.")
        .attribute("to", Attributes.region(true, "Region this executor acts inside of."))
        .build();
  }

  public static Executor parse(Match match, XmlElement element) throws XmlException {
    Check check = FactoryUtils
        .resolveRequiredCheckChild(match, element.getAttribute("check"), element.getChild("check"));
    String id = element.getAttribute("id").asString().orElse(UUID.randomUUID().toString());
    BoundedRegion region = FactoryUtils
        .resolveRequiredRegionAs(match, BoundedRegion.class, element.getAttribute("to"),
            element.getChild("to"));
    Optional<AngleProvider> yaw = element.getAttribute("yaw").asFloat()
        .map(StaticAngleProvider::new);
    Optional<AngleProvider> pitch = element.getAttribute("pitch").asFloat()
        .map(StaticAngleProvider::new);
    return new TeleportExecutor(id, check, region, yaw, pitch);
  }

  @Override
  public void execute(CheckContext context) {
    Player player = context.getLast(PlayerVariable.class).map(PlayerVariable::getPlayer)
        .orElse(null);
    if (player != null) {
      Vector position = this.region.getRandomPosition(random);
      final Location previous = player.getLocation();
      player.teleport(position.toLocation(player.getWorld(),
          this.yaw.map(ap -> ap.getAngle(position)).orElse(previous.getYaw()),
          this.pitch.map(ap -> ap.getAngle(position)).orElse(previous.getPitch())));
    }
  }
}
