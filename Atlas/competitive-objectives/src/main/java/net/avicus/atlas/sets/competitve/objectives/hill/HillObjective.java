package net.avicus.atlas.sets.competitve.objectives.hill;

import com.google.common.collect.ArrayListMultimap;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.objectives.GlobalObjective;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.shop.PlayerEarnPointEvent;
import net.avicus.atlas.sets.competitve.objectives.hill.event.HillCaptureEvent;
import net.avicus.atlas.sets.competitve.objectives.hill.event.HillCompletionChangeEvent;
import net.avicus.atlas.sets.competitve.objectives.hill.event.HillOwnerChangeEvent;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.atlas.util.Players;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.inventory.MultiMaterialMatcher;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedText;
import net.avicus.magma.util.region.BoundedRegion;
import net.avicus.magma.util.region.Region;
import net.avicus.magma.util.region.special.SectorRegion;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import org.joda.time.Duration;
import org.joda.time.Seconds;

/**
 * Represents a place in the match that can be controlled by a competitor.
 */
@Getter
@ToString(exclude = "match")
public class HillObjective implements Objective, GlobalObjective {

  /**
   * Match this objective exists in.
   **/
  private final Match match;
  /**
   * Name of the hill.
   **/
  private final LocalizedXmlString name;
  /**
   * Region the competitors must be standing in to capture this hill.
   **/
  private final Region capture;
  /**
   * Region that displays the progress of the hill.
   **/
  private final BoundedRegion progress;
  /**
   * Rule used to determine who should capture when multiple competitors are on the hill.
   **/
  private final HillCaptureRule captureRule;
  /**
   * How long it takes this hill to go from neutral to completely captured.
   **/
  private final Duration captureTime;
  /**
   * Check to be run before a competitor can begin capturing.
   **/
  private final Optional<Check> captureCheck;
  /**
   * Owner of the hill when the match starts.
   **/
  private final Optional<Team> initialOwner;
  /**
   * Points the competitor will earn when standing on the captured hill.
   **/
  private final Optional<Integer> points;
  /**
   * How much the points should be multiplied by based on control time.
   **/
  private final Optional<Duration> pointsGrowth;
  /**
   * If fireworks should explode when the hill is captured.
   **/
  private final boolean fireworks;
  /**
   * If the hill can have only one owner in it's lifetime.
   **/
  private final boolean permanent;
  /**
   * If the hill should return to neutral if there are no competitors standing on it.
   **/
  private final boolean depreciate;
  /**
   * Block types that are ignored in the progress region.
   **/
  private final Optional<MultiMaterialMatcher> ignoredBlocks;

  /**
   * The rule that should be used to determine when points should be earned for the owning team.
   */
  private final PointEarnRule earnRule;

  /**
   * Current owner.
   **/
  private Optional<Competitor> owner;
  /**
   * Competitors standing on the hill.
   **/
  private ArrayListMultimap<Competitor, Player> capturing;
  /**
   * Competitor who is capturing the hill.
   **/
  private Optional<Competitor> capturingTeam = Optional.empty();
  /**
   * Amount this hill is captured.
   **/
  private double completion;
  /**
   * How long the current owner has controlled the hill.
   **/
  private Optional<Duration> controlTime = Optional.empty();

  /**
   * Constructor
   *
   * @param match Match this objective exists in.
   * @param name Name of the hill.
   * @param capture Region the competitors must be standing in to capture this hill.
   * @param progress Region that displays the progress of the hill.
   * @param captureRule Rule used to determine who should capture when multiple competitors are on
   * the hill.
   * @param captureTime How long it takes this hill to go from neutral to completely captured.
   * @param captureCheck Check to be run before a competitor can begin capturing.
   * @param initialOwner Owner of the hill when the match starts.
   * @param points Points the competitor will earn when standing on the captured hill.
   * @param pointsGrowth How much the points should be multiplied by based on control time.
   * @param fireworks If fireworks should explode when the hill is captured.
   * @param permanent If the hill can have only one owner in it's lifetime.
   * @param depreciate If the hill should return to neutral if there are no competitors standing on
   * it.
   * @param ignoredBlocks Block types that are ignored in the progress region.
   * @param earnRule The rule that should be used to determine when points should be earned for the
   * owning team.
   */
  public HillObjective(Match match,
      LocalizedXmlString name,
      Region capture,
      BoundedRegion progress,
      HillCaptureRule captureRule,
      Duration captureTime,
      Optional<Check> captureCheck,
      Optional<Team> initialOwner,
      Optional<Integer> points,
      Optional<Duration> pointsGrowth,
      boolean fireworks,
      boolean permanent,
      boolean depreciate,
      Optional<MultiMaterialMatcher> ignoredBlocks,
      PointEarnRule earnRule) {
    this.match = match;
    this.name = name;
    this.capture = capture;
    this.progress = progress;
    this.captureRule = captureRule;
    this.captureTime = captureTime;
    this.captureCheck = captureCheck;
    this.initialOwner = initialOwner;
    this.points = points;
    this.pointsGrowth = pointsGrowth;
    this.fireworks = fireworks;
    this.permanent = permanent;
    this.depreciate = depreciate;
    this.ignoredBlocks = ignoredBlocks;
    this.earnRule = earnRule;

    this.owner = Optional.ofNullable(initialOwner.orElse(null));
    this.completion = initialOwner.isPresent() ? 1.0 : 0;
    this.capturing = ArrayListMultimap.create();

    if (this.initialOwner.isPresent()) {
      this.controlTime = Optional.of(new Duration(1));
    }
  }

  public void tick(int interval) {
    if (isCompleted() && isPermanent()) {
      return;
    }

    Optional<Competitor> lastCapturingTeam = this.capturingTeam;
    Optional<Competitor> capturingTeam = getCapturing();
    this.capturingTeam = capturingTeam;

    double change = (interval * 50.0) / (double) this.captureTime.getMillis();

    double newCompletion = this.completion;
    Optional<Competitor> newOwner = this.owner;

    if (capturingTeam.isPresent()) {
      if (this.owner.isPresent()) {
        if (capturingTeam.equals(this.owner)) {
          newCompletion += change;
        } else {
          // different capturing team
          newCompletion -= change;

          if (newCompletion < 0) {
            newOwner = capturingTeam;
          }
        }
      } else {
        newOwner = capturingTeam;
        newCompletion += change;
      }
    } else if (this.owner.isPresent() && this.capturing.size() == 0) {
      if (this.depreciate) {
        newCompletion -= change;
        if (newCompletion <= 0) {
          newOwner = Optional.empty();
        }
      } else if (this.earnRule == PointEarnRule.ALWAYS
          || this.earnRule == PointEarnRule.STANDING_OFF) {
        reward();
        if (this.earnRule == PointEarnRule.ALWAYS) {
          if (this.controlTime.isPresent()) {
            this.controlTime = Optional.of(this.controlTime.get().plus(interval * 50));
          } else {
            this.controlTime = Optional.of(new Duration(0));
          }
        }
        return;
      } else {
        return;
      }
    }

    newCompletion = Math.min(1.0, Math.max(newCompletion, 0.0));

    if (this.completion != newCompletion || !this.owner.equals(newOwner) || !lastCapturingTeam
        .equals(capturingTeam)) {
      HillCompletionChangeEvent call = new HillCompletionChangeEvent(this, this.completion,
          newCompletion);
      Events.call(call);

      if (!this.owner.equals(newOwner)) {
        HillOwnerChangeEvent ownerChangeEvent = new HillOwnerChangeEvent(this,
            this.capturing.get(newOwner.orElse(null)), this.owner, newOwner);
        Events.call(ownerChangeEvent);
      }

      this.completion = newCompletion;
      this.owner = newOwner;
      updateProgress();
    }

    // owner is present & completion = 1.0
    if (this.completion == 1.0) {
      if (this.controlTime.isPresent()) {
        this.controlTime = Optional.of(this.controlTime.get().plus(interval * 50));
      } else {
        this.controlTime = Optional.of(new Duration(0));
      }

      if (this.earnRule == PointEarnRule.ALWAYS || this.earnRule == PointEarnRule.STANDING_ON) {
        // Reward players
        if (capturingTeam.equals(this.owner)) {
          reward();
        }
      }

      // Fireworks!
      Duration delay = Seconds.seconds(1).toStandardDuration();
      if (this.fireworks && this.controlTime.get().equals(delay)) {
        spawnFirework();
      }

      if (this.controlTime.get().equals(delay)) {
        Localizable hillName = this.name.toText();
        Localizable teamName = this.owner.get().getName().toText();

        hillName.style().color(this.owner.get().getGroup().getChatColor());
        teamName.style().color(this.owner.get().getGroup().getChatColor());

        LocalizedText broadcast = Messages.GENERIC_OBJECTIVE_CAPTURED.with(hillName, teamName);
        broadcast = Messages.UI_IMPORTANT.with(TextStyle.ofBold(), broadcast);
        this.match.broadcast(broadcast);

        HillCaptureEvent captureEvent = new HillCaptureEvent(
            this.capturing.get(newOwner.orElse(null)), this, newOwner);
        Events.call(captureEvent);
        this.capturing.get(newOwner.orElse(null))
            .forEach(p -> Events.call(new PlayerEarnPointEvent(p, "hill-capture")));
      }

    } else {
      this.controlTime = Optional.empty();
    }
  }

  /**
   * Reward the current owner of the hill (if they are standing on it).
   */
  private void reward() {
    if (this.points.isPresent() && this.controlTime.isPresent()
        && this.controlTime.get().getMillis() != 0) {
      int reward = this.points.get();

      // double if points growth enabled
      if (this.pointsGrowth.isPresent()) {
        double doubles =
            (double) this.controlTime.get().getMillis() / (double) this.pointsGrowth.get()
                .getMillis();
        while (doubles >= 1) {
          reward *= 2;
          doubles -= 1;
        }
      }

      // reward points if one second has gone by
      if (this.controlTime.get().getMillis() % 1000 == 0) {
        this.match.getRequiredModule(ObjectivesModule.class).score(this.owner.get(), reward);
      }
    }
  }

  /**
   * Get a map of competitor -> player standing on the hill at this time.
   */
  public ArrayListMultimap<Competitor, Player> getCapturingPlayers() {
    return this.capturing;
  }

  /**
   * Get the competitor that is currently capturing the hill.
   * This uses the {@link HillCaptureRule} to determine who should be selected.
   *
   * @return current competitor who is capturing the hill.
   */
  public Optional<Competitor> getCapturing() {
    Optional<Competitor> capturing = Optional.empty();

    if (isCompleted() && isPermanent()) {
      return this.capturingTeam;
    }

    switch (this.captureRule) {
      case EXCLUSIVE:
        if (this.capturing.keySet().size() == 1) {
          capturing = Optional.of(this.capturing.keys().iterator().next());
        }
        break;
      case MAJORITY:
        if (this.capturing.size() > 0) {
          int total = this.capturing.values().size();
          for (Competitor test : this.capturing.keySet()) {
            double portion = (double) this.capturing.get(test).size() / (double) total;
            if (portion > 0.5001) {
              capturing = Optional.of(test);
              break;
            }
          }
        }
        break;
      case MOST:
        if (this.capturing.size() > 0) {
          Competitor most = null;
          int mostCount = Integer.MIN_VALUE;

          for (Competitor test : this.capturing.keySet()) {
            int testCount = this.capturing.get(test).size();
            if (testCount > mostCount) {
              most = test;
              mostCount = testCount;
            } else if (mostCount == testCount) {
              most = null;
              mostCount = Integer.MIN_VALUE;
            }
          }

          capturing = Optional.ofNullable(most);
        }
        break;
      default:
        throw new RuntimeException();
    }
    return capturing;
  }

  /**
   * Spawn firework above the center of the progress region with the color of the current owner.
   *
   * @return the spawned firework.
   */
  private Firework spawnFirework() {
    Location location = this.progress.getCenter().toLocation(this.match.getWorld()).add(0, 2, 0);
    Firework firework = (Firework) this.match.getWorld().spawnEntity(location, EntityType.FIREWORK);
    FireworkMeta meta = firework.getFireworkMeta();
    meta.setPower(0);

    FireworkEffect.Builder builder = FireworkEffect.builder();
    builder.with(FireworkEffect.Type.BURST);
    builder.withColor(this.owner.get().getGroup().getFireworkColor());
    builder.withTrail();

    meta.addEffect(builder.build());
    firework.setFireworkMeta(meta);

    firework.setVelocity(firework.getVelocity().multiply(0.7));

    // 1.8-1.9 Support
    Players.playFireworkSound();

    return firework;
  }

  /**
   * Update the progress region based on current completion and owner.
   */
  private void updateProgress() {
    SectorRegion sector = getProgressSector();

    for (Vector point : this.progress) {
      Block block = point.toLocation(this.match.getWorld()).getBlock();
      boolean colorable = (block.getType() == Material.WOOL ||
          block.getType() == Material.STAINED_CLAY ||
          block.getType() == Material.STAINED_GLASS ||
          block.getType() == Material.STAINED_GLASS_PANE ||
          block.getType() == Material.CARPET);

      if (colorable && this.ignoredBlocks.isPresent()) {
        colorable = !this.ignoredBlocks.get().matches(block.getState());
      }

      if (colorable) {
        byte data =
            sector.contains(point) ? this.owner.get().getGroup().getDyeColor().getWoolData() : 0;
        block.setData(data);
      }
    }
  }

  /**
   * @return the sector of the progress region that should display capture status based on
   * completion.
   */
  private SectorRegion getProgressSector() {
    Vector center = this.progress.getCenter();
    double percent = this.completion * 100;
    double start = 0;
    double end = (percent / 100.0) * 360.0;
    return new SectorRegion(center.getX(), center.getZ(), start, end);
  }

  /**
   * Check if the player is capturing the hill.
   */
  public boolean isCapturing(Player player) {
    return this.capturing.values().contains(player);
  }

  /**
   * Add a player to the list of currently capturing players.
   */
  public void add(Player player) {
    if (!isCapturing(player)) {
      Competitor competitor = this.match.getRequiredModule(GroupsModule.class)
          .getCompetitorOf(player).orElse(null);
      if (competitor != null) {
        this.capturing.put(competitor, player);
      }
    }
  }

  /**
   * Remove a player from the list of currently capturing players.
   */
  public void remove(Player player) {
    this.capturing.values().remove(player);
  }

  /**
   * @return if the player is able to capture the hill.
   */
  public boolean canCapture(Player player) {
    if (this.captureCheck.isPresent()) {
      CheckContext context = new CheckContext(this.match);
      context.add(new PlayerVariable(player));

      return this.captureCheck.get().test(context).passes();
    }

    return true;
  }

  @Override
  public void initialize() {
    updateProgress();
  }

  @Override
  public boolean canComplete(Competitor competitor) {
    return !competitor.getGroup().isSpectator();
  }

  @Override
  public boolean isCompleted(Competitor competitor) {
    return isCompleted() && this.owner.isPresent() && this.owner.get().equals(competitor);
  }

  @Override
  public double getCompletion(Competitor competitor) {
    return getCompletion();
  }

  public boolean isCompleted() {
    return this.completion == 1.0;
  }

  @Override
  public boolean isIncremental() {
    return true;
  }
}
