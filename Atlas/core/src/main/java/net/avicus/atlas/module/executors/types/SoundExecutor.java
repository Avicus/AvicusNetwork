package net.avicus.atlas.module.executors.types;

import java.util.Optional;
import java.util.UUID;
import lombok.ToString;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.EnumAttribute;
import net.avicus.atlas.documentation.attributes.RangeAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.executors.Executor;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.magma.util.region.shapes.PointRegion;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * An executor that plays a sound to a player.
 * If no location is supplied, a player's location will be used.
 */
@ToString
public class SoundExecutor extends Executor {

  private final Sound sound;
  private final float volume;
  private final float pitch;
  private final Optional<Location> location;

  public SoundExecutor(String id, Check check, Sound sound, float volume, float pitch,
      Optional<Location> location) {
    super(id, check);
    this.sound = sound;
    this.volume = volume;
    this.pitch = pitch;
    this.location = location;
  }

  public static FeatureDocumentation documentation() {
    return FeatureDocumentation.builder()
        .name("Play Sound")
        .tagName("play-sound")
        .description("An executor that plays a sound to a player.")
        .attribute("sound", new EnumAttribute(Sound.class, true, "Type of sound to be played."))
        .attribute("volume", new RangeAttribute(0, 2.0, true, "Volume of the sound."))
        .attribute("pitch", new RangeAttribute(0, 2.0, true, "Pitch of the sound."))
        .attribute("region", Attributes.region(false, "Region this executor acts inside of."))
        .build();
  }

  public static Executor parse(Match match, XmlElement element) throws XmlException {
    Check check = FactoryUtils
        .resolveRequiredCheckChild(match, element.getAttribute("check"), element.getChild("check"));
    String id = element.getAttribute("id").asString().orElse(UUID.randomUUID().toString());
    Sound sound = element.getAttribute("sound").asRequiredEnum(Sound.class, true);
    float volume = new Double(element.getAttribute("volume").asRequiredDouble()).floatValue();
    float pitch = new Double(element.getAttribute("pitch").asRequiredDouble()).floatValue();
    Optional<Location> location = Optional.empty();
    Optional<PointRegion> region = FactoryUtils
        .resolveRegionAs(match, PointRegion.class, element.getAttribute("location"),
            element.getChild("location"));
    if (region.isPresent()) {
      location = Optional.of(region.get().getMax().toLocation(match.getWorld()));
    }
    return new SoundExecutor(id, check, sound, volume, pitch, location);
  }

  @Override
  public void execute(CheckContext context) {
    Player player = context.getLast(PlayerVariable.class).map(PlayerVariable::getPlayer)
        .orElse(null);
    if (player != null) {
      player.playSound(this.location.orElse(player.getLocation()), this.sound, this.volume,
          this.pitch);
    }
  }
}
