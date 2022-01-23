package net.avicus.atlas.sets.competitve.objectives.zones.flag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.registry.WeakReference;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.shop.PlayerEarnPointEvent;
import net.avicus.atlas.module.zones.Zone;
import net.avicus.atlas.module.zones.ZoneMessage;
import net.avicus.atlas.runtimeconfig.fields.ConfigurableField;
import net.avicus.atlas.runtimeconfig.fields.DurationField;
import net.avicus.atlas.runtimeconfig.fields.OptionalField;
import net.avicus.atlas.runtimeconfig.fields.RegisteredObjectField;
import net.avicus.atlas.runtimeconfig.fields.SimpleFields.IntField;
import net.avicus.atlas.sets.competitve.objectives.flag.FlagCountdown;
import net.avicus.atlas.sets.competitve.objectives.flag.FlagObjective;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagCaptureEvent;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.util.region.Region;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.joda.time.Duration;

@ToString
public class NetZone extends Zone {

  private final Optional<Team> owner;
  private Optional<Integer> points;
  private final Optional<WeakReference<PostZone>> post;
  private final Optional<List<WeakReference<FlagObjective>>> flags;
  private Optional<Check> captureCheck;
  private final boolean respawnTogether;
  private Optional<Duration> respawnDelay;

  private List<FlagObjective> respawnQueue = new ArrayList<>();

  public NetZone(Match match,
      Region region,
      Optional<ZoneMessage> message,
      Optional<Team> owner,
      Optional<Integer> points,
      Optional<WeakReference<PostZone>> post,
      Optional<List<WeakReference<FlagObjective>>> flags,
      Optional<Check> captureCheck,
      boolean respawnTogether,
      Optional<Duration> respawnDelay) {
    super(match, region, message);
    this.owner = owner;
    this.points = points;
    this.post = post;
    this.flags = flags;
    this.captureCheck = captureCheck;
    this.respawnTogether = respawnTogether;
    this.respawnDelay = respawnDelay;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  public void capture(Player who, FlagObjective flag) {
    Team team = null;

    if (flag.getCarrierHelmet().isPresent()) {
      who.getInventory().setHelmet(flag.getCarrierHelmet().get());
    }

    if (this.owner.isPresent()) {
      team = this.owner.get();
    } else if (flag.getCarrier().isPresent()) {
      Group group = flag.getCarrier().get().getGroup();
      if (group instanceof Team) {
        team = (Team) group;
      }
    }

    if (team == null) {
      throw new RuntimeException("No team to reward points to for flag capture.");
    }

    Localizable flagText = flag.getName().toText(flag.getChatColor());
    Localizable whoText = new UnlocalizedText(who.getName(), team.getChatColor());

    if (this.points.isPresent()) {
      this.match.getRequiredModule(ObjectivesModule.class).score(team, this.points.get(), who);
    }

    Events.call(new FlagCaptureEvent(who, flag));
    Events.call(new PlayerEarnPointEvent(who, "flag-capture"));

    Localizable broadcast = Messages.GENERIC_OBJECTIVE_CAPTURED.with(flagText, whoText);
    broadcast = Messages.UI_IMPORTANT.with(TextStyle.ofBold(), broadcast);
    this.match.broadcast(broadcast);
  }

  private void respawn(FlagObjective flag) {
    PostZone post = flag.getPost();
    if (this.post.isPresent()) {
      Optional<PostZone> reference = this.post.get().getObject();
      if (reference.isPresent()) {
        post = reference.get();
      }
    }

    if (this.respawnDelay.isPresent()) {
      FlagCountdown countdown = new FlagCountdown(this.match, this.respawnDelay.get(), flag, post);
      flag.setFlagCountdown(Optional.of(countdown));
      flag.getCm().start(countdown);
    } else {
      post.spawn(flag, true);
    }
  }

  public void queueRespawn(FlagObjective flag) {
    this.respawnQueue.add(flag);
    attemptRespawn();
  }

  private void attemptRespawn() {
    boolean respawn = true;

    if (this.respawnTogether) {
      List<FlagObjective> flagsToCapture = new ArrayList<>();
      if (this.flags.isPresent()) {
        flagsToCapture.addAll(this.flags.get()
            .stream()
            .filter(reference -> reference.isPresent())
            .map(reference -> reference.getObject()
                .get())
            .collect(Collectors.toList()));
      } else {
        List<FlagObjective> all = this.match.getModule(ObjectivesModule.class).get()
            .getObjectivesByType(FlagObjective.class);
        flagsToCapture.addAll(all);
      }

      respawn = flagsToCapture.size() == this.respawnQueue.size();
    }

    if (respawn) {
      this.respawnQueue.forEach(this::respawn);
      this.respawnQueue.clear();
    }
  }

  public boolean canCapture(Player player, FlagObjective flag) {
    boolean validFlag = false;

    if (this.flags.isPresent()) {
      for (WeakReference<FlagObjective> reference : this.flags.get()) {
        Optional<FlagObjective> optional = reference.getObject();
        if (optional.isPresent()) {
          if (optional.get().equals(flag)) {
            validFlag = true;
            break;
          }
        }
      }
    } else {
      validFlag = true;
    }

    if (!validFlag) {
      return false;
    }

    if (this.owner.isPresent() && !this.owner.get().hasPlayer(player)) {
      return false;
    }

    if (this.captureCheck.isPresent()) {
      CheckContext context = new CheckContext(this.match);
      context.add(new LocationVariable(player.getLocation()));
      context.add(new PlayerVariable(player));
      // todo: flag variable?
      return this.captureCheck.get().test(context).passes();
    } else {
      return true;
    }
  }

  @Override
  public ConfigurableField[] getFields() {
    return ArrayUtils.addAll(super.getFields(),
        new OptionalField<>("Points", () -> this.points, (v) -> this.points = v, new IntField("points")),
        new OptionalField<>("Capture Check", () -> this.captureCheck, (v) -> this.captureCheck = v, new RegisteredObjectField<>("check", Check.class)),
        new OptionalField<>("Respawn Delay", () -> this.respawnDelay, (v) -> this.respawnDelay = v, new DurationField("delay"))
    );
  }

  @Override
  public String getDescription(CommandSender viewer) {
    return "Flag Net" + super.getDescription(viewer);
  }
}
