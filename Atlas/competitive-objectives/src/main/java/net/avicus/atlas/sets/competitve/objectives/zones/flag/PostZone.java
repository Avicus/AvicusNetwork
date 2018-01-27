package net.avicus.atlas.sets.competitve.objectives.zones.flag;

import java.util.Optional;
import java.util.Random;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.zones.Zone;
import net.avicus.atlas.module.zones.ZoneMessage;
import net.avicus.atlas.sets.competitve.objectives.flag.FlagObjective;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.magma.util.region.BoundedRegion;
import org.bukkit.entity.Player;

@ToString
public class PostZone extends Zone {

  private static final Random random = new Random();

  private final float yaw;
  private final Optional<Check> pickupCheck;

  public PostZone(Match match, BoundedRegion region, Optional<ZoneMessage> message, float yaw,
      Optional<Check> pickupCheck) {
    super(match, region, message);
    this.yaw = yaw;
    this.pickupCheck = pickupCheck;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  public boolean canPickup(Player player) {
    if (!this.pickupCheck.isPresent()) {
      return true;
    }

    CheckContext context = new CheckContext(this.match);
    context.add(new PlayerVariable(player));
    context.add(new LocationVariable(player.getLocation()));
    return this.pickupCheck.get().test(context).passes();
  }

  public void spawn(FlagObjective flag, boolean shouldBroadcast) {
    if (shouldBroadcast) {
      Localizable name = flag.getName().toText(flag.getChatColor());
      Localizable broadcast = Messages.GENERIC_OBJECTIVE_RESPAWNED.with(name);
      this.match.broadcast(broadcast);
    }
    flag.setCurrentPost(Optional.of(this));
    flag.placeFlag(this.region.getRandomPosition(random), this.yaw, true);
  }
}
