package net.avicus.atlas.module.structures;

import java.util.Optional;
import java.util.UUID;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.executors.Executor;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.magma.util.region.BoundedRegion;
import org.bukkit.Location;
import org.joda.time.Duration;

/**
 * An executor that spawns a loaded schematic at a location.
 * If no location is set, a player's location will be used.
 */
@ToString(exclude = "match")
public class PasteSchematicExecutor extends Executor {

  private final Match match;
  private final Schematic schematic;
  private final boolean natural;
  private final boolean random;
  private final boolean ignoreAir;
  private final int delay;
  private final Optional<BoundedRegion> region;

  public PasteSchematicExecutor(String id, Check check, Match match, Schematic schematic) {
    super(id, check);
    this.match = match;
    this.schematic = schematic;
    this.delay = 0;
    this.natural = false;
    this.random = false;
    this.ignoreAir = false;
    this.region = Optional.empty();
  }

  public PasteSchematicExecutor(String id, Check check, Match match, Schematic schematic,
      boolean natural, boolean random, boolean ignoreAir, int delay,
      Optional<BoundedRegion> region) {
    super(id, check);
    this.match = match;
    this.schematic = schematic;
    this.natural = natural;
    this.random = random;
    this.ignoreAir = ignoreAir;
    this.delay = delay;
    this.region = region;
  }

  public static Executor parse(Match match, XmlElement element) throws XmlException {
    Check check = FactoryUtils
        .resolveRequiredCheckChild(match, element.getAttribute("check"), element.getChild("check"));
    String id = element.getAttribute("id").asString().orElse(UUID.randomUUID().toString());
    Schematic schematic = match.getRegistry()
        .get(Schematic.class, element.getAttribute("schematic").asRequiredString(), true).get();
    boolean natural = element.getAttribute("natural").asBoolean().orElse(false);
    boolean random = element.getAttribute("random").asBoolean().orElse(false);
    boolean ignoreAir = element.getAttribute("ignore-air").asBoolean().orElse(true);
    int delay =
        ((int) element.getAttribute("paste-delay").asDuration().orElse(Duration.ZERO).getMillis())
            / 50;
    Optional<BoundedRegion> region = FactoryUtils
        .resolveRegionAs(match, BoundedRegion.class, element.getAttribute("region"),
            element.getChild("region"));
    return new PasteSchematicExecutor(id, check, match, schematic, natural, random, ignoreAir,
        delay, region);
  }

  @Override
  public void execute(CheckContext context) {
    Location location = context.getFirst(LocationVariable.class).map(LocationVariable::getLocation)
        .orElse(null);
    if (this.region.isPresent()) {
      location = region.get().getCenter().toLocation(this.match.getWorld());
    }
    if (location != null) {
      schematic.paste(location, this.natural, this.random, this.ignoreAir, this.delay);
    }
  }
}
