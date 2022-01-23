package net.avicus.atlas.module.zones.zones;

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
import net.avicus.atlas.runtimeconfig.fields.AngleProviderField;
import net.avicus.atlas.runtimeconfig.fields.ConfigurableField;
import net.avicus.atlas.runtimeconfig.fields.OptionalField;
import net.avicus.atlas.runtimeconfig.fields.RegisteredObjectField;
import net.avicus.atlas.runtimeconfig.fields.SimpleFields.BooleanField;
import net.avicus.compendium.points.AngleProvider;
import net.avicus.magma.util.region.BoundedRegion;
import net.avicus.magma.util.region.Region;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;
import tc.oc.tracker.event.PlayerCoarseMoveEvent;

@ToString(callSuper = true)
public class TransportZone extends Zone {

  private static final Random random = new Random();

  private BoundedRegion destination;
  private Optional<Check> check;
  private final boolean sound;
  private final boolean resetVelocity;
  private boolean heal;
  private boolean feed;
  private Optional<AngleProvider> yaw;
  private Optional<AngleProvider> pitch;

  public TransportZone(Match match, Region region, Optional<ZoneMessage> message,
      BoundedRegion destination, Optional<Check> check, boolean sound, boolean resetVelocity,
      boolean heal, boolean feed, Optional<AngleProvider> yaw, Optional<AngleProvider> pitch) {
    super(match, region, message);
    this.destination = destination;
    this.check = check;
    this.sound = sound;
    this.resetVelocity = resetVelocity;
    this.heal = heal;
    this.feed = feed;
    this.yaw = yaw;
    this.pitch = pitch;
  }

  @Override
  public boolean isActive() {
    return this.destination != null;
  }

  public void transport(Player player) {
    Location from = player.getLocation();

    Location to = this.destination.getRandomPosition(random).toLocation(player.getWorld());
    float yaw = from.getYaw();
    float pitch = from.getPitch();

    if (this.yaw.isPresent()) {
      yaw = this.yaw.get().getAngle(to.toVector());
    }

    if (this.pitch.isPresent()) {
      pitch = this.pitch.get().getAngle(to.toVector());
    }

    to.setYaw(yaw);
    to.setPitch(pitch);
    player.teleport(to);

    if (this.resetVelocity) {
      player.setVelocity(new Vector());
    }

    if (this.heal) {
      player.setHealth(player.getMaxHealth());
    }

    if (this.feed) {
      player.setSaturation(20);
      player.setFoodLevel(20);
    }

    if (this.sound) {
      player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 0.7F, 1F);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onTP(PlayerTeleportEvent event) {
    handle(event.getPlayer(), event.getFrom(), event.getTo());
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onMove(PlayerCoarseMoveEvent event) {
    handle(event.getPlayer(), event.getFrom(), event.getTo());
  }

  public void handle(Player player, Location fromLoc, Location toLoc) {
    if (isObserving(this.match, player)) {
      return;
    }

    boolean from = getRegion().contains(fromLoc);

    if (from) {
      return;
    }

    boolean to = getRegion().contains(toLoc);

    if (to) {
      if (this.check.isPresent()) {
        CheckContext context = new CheckContext(this.match);
        context.add(new PlayerVariable(player));
        context.add(new LocationVariable(toLoc));
        if (this.check.get().test(context).fails()) {
          return;
        }
      }

      transport(player);
    }
  }

  @Override
  public String getDescription(CommandSender viewer) {
    return "Portal" + super.getDescription(viewer);
  }

  @Override
  public ConfigurableField[] getFields() {
    return ArrayUtils.addAll(super.getFields(),
        new RegisteredObjectField<BoundedRegion>("Destination", () -> this.destination, (v) -> this.destination = v, BoundedRegion.class),
        new OptionalField<>("Use Check", () -> this.check, (v) -> this.check = v, new RegisteredObjectField<>("Use Check", Check.class)),
        new BooleanField("Heal", () -> this.heal, (v) -> this.heal = v),
        new BooleanField("Feed", () -> this.feed, (v) -> this.feed = v),
        new OptionalField<>("Yaw", () -> this.yaw, (v) -> this.yaw = v, new AngleProviderField("yaw")),
        new OptionalField<>("Pitch", () -> this.pitch, (v) -> this.pitch = v, new AngleProviderField("yaw"))
    );
  }
}
