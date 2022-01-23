package net.avicus.atlas.sets.competitve.objectives.flag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.objectives.GlobalObjective;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.objectives.locatable.LocatableObjective;
import net.avicus.atlas.module.objectives.score.ScoreObjective;
import net.avicus.atlas.module.shop.PlayerEarnPointEvent;
import net.avicus.atlas.module.zones.ZonesModule;
import net.avicus.atlas.runtimeconfig.fields.ConfigurableField;
import net.avicus.atlas.runtimeconfig.fields.DurationField;
import net.avicus.atlas.runtimeconfig.fields.EnumField;
import net.avicus.atlas.runtimeconfig.fields.OptionalField;
import net.avicus.atlas.runtimeconfig.fields.RegisteredObjectField;
import net.avicus.atlas.runtimeconfig.fields.SimpleFields.BooleanField;
import net.avicus.atlas.runtimeconfig.fields.SimpleFields.IntField;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagDropEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagPickupEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagStealEvent;
import net.avicus.atlas.sets.competitve.objectives.zones.flag.NetZone;
import net.avicus.atlas.sets.competitve.objectives.zones.flag.PostZone;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.atlas.util.ScopableItemStack;
import net.avicus.atlas.util.Worlds;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.countdown.CountdownManager;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedFormat;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.plugin.CompendiumPlugin;
import net.avicus.compendium.utils.Strings;
import net.avicus.magma.util.distance.DistanceCalculationMetric;
import net.avicus.magma.util.region.BoundedRegion;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.util.Vector;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;


@Getter
@ToString(exclude = "match")
public class FlagObjective extends LocatableObjective implements GlobalObjective, Objective {

  private final Random random = new Random();

  private final Match match;
  private final FlagDistanceMetrics metrics;
  private final PostZone post;
  private final Optional<Team> owner;
  private final Optional<DyeColor> color;
  private final Optional<ScopableItemStack> banner;
  private Optional<Check> carryCheck;
  private Duration recoverTime;
  private final boolean highlightHolder;
  private final Optional<Duration> highlightDelay;  // Only delays when flag is not on post/ground.
  private boolean permanent;
  private int carryingPoints;
  private Optional<Duration> carryingPointsDelay;
  private Optional<Duration> carryingPointsGrowth;
  private FlagPickupMethod pickupMethod;

  private final CountdownManager cm = CompendiumPlugin.getInstance().getCountdownManager();
  private final List<NetZone> nets;
  @Setter
  private LocalizedXmlString name;
  private DyeColor baseColor;
  private List<Pattern> patterns;
  private ScopableItemStack itemStack;
  private Optional<Block> currentPlacement = Optional.empty();
  private Optional<ItemStack> carrierHelmet = Optional.empty();
  private Optional<Player> carrier = Optional.empty();
  @Setter
  private Optional<DateTime> pickupDate = Optional.empty();

  private Optional<Competitor> mostRecentCapture;

  @Setter
  private Optional<FlagCountdown> flagCountdown = Optional.empty();
  @Setter
  private Optional<PostZone> currentPost = Optional.empty();

  private Instant lastReward;

  public FlagObjective(Match match,
      FlagDistanceMetrics metrics,
      PostZone post,
      Optional<Team> owner,
      Optional<DyeColor> color,
      Optional<ScopableItemStack> banner,
      Optional<Check> carryCheck,
      Duration recoverTime,
      boolean highlightHolder,
      Optional<Duration> highlightDelay,
      boolean permanent,
      int carryingPoints,
      Optional<Duration> carryingPointsDelay,
      Optional<Duration> carryingPointsGrowth,
      FlagPickupMethod pickupMethod) {
    super(metrics, match);
    this.match = match;
    this.metrics = metrics;
    this.post = post;
    this.owner = owner;
    this.color = color;
    this.banner = banner;
    this.carryCheck = carryCheck;
    this.recoverTime = recoverTime;
    this.highlightHolder = highlightHolder;
    this.highlightDelay = highlightDelay;
    this.permanent = permanent;
    this.carryingPoints = carryingPoints;
    this.carryingPointsDelay = carryingPointsDelay;
    this.carryingPointsGrowth = carryingPointsGrowth;
    this.pickupMethod = pickupMethod;

    this.name = new LocalizedXmlString("flag not init");
    this.baseColor = color.isPresent() ? color.get() : DyeColor.WHITE;
    this.patterns = new ArrayList<>();
    this.currentPost = Optional.of(post);

    this.nets = match.getRequiredModule(ZonesModule.class).getZones(NetZone.class);
  }

  private void resolveBannerDesign() {
    if (this.banner.isPresent()) {
      BannerMeta meta = (BannerMeta) this.banner.get().getItemStack().getItemMeta();
      this.baseColor = meta.getBaseColor();
      this.patterns = meta.getPatterns();
    } else {
      for (Vector vector : (BoundedRegion) this.post.getRegion()) {
        Block block = vector.toLocation(this.match.getWorld()).getBlock();
        if (block.getState() instanceof Banner) {
          Banner banner = (Banner) block.getState();
          this.baseColor = banner.getBaseColor();
          this.patterns = banner.getPatterns();
          block.setType(Material.AIR);
        }
      }
    }

    this.name = new LocalizedXmlString(Messages.forFlagColor(getColor()));

    ItemStack item = new ItemStack(Material.BANNER);
    BannerMeta meta = (BannerMeta) item.getItemMeta();
    meta.setBaseColor(this.baseColor);
    meta.setPatterns(this.patterns);
    item.setItemMeta(meta);

    this.itemStack = new ScopableItemStack(this.getMatch(), item);
  }

  @Override
  public void initialize() {
    resolveBannerDesign();
    this.post.spawn(this, false);
  }

  public void remove() {
    if (this.currentPlacement.isPresent()) {
      this.currentPlacement.get().setType(Material.AIR);
    }

    if (this.carrier.isPresent() && this.carrierHelmet.isPresent()) {
      this.carrier.get().getInventory().setHelmet(this.carrierHelmet.get());
    }

    this.currentPlacement = Optional.empty();
    this.carrier = Optional.empty();
    this.carrierHelmet = Optional.empty();
    this.pickupDate = Optional.empty();
  }

  /**
   * Places the flag in the world.
   */
  public void placeFlag(Vector vector, float yaw, boolean updateBlock) {
    Block block = vector.toLocation(this.match.getWorld()).getBlock();

    if (updateBlock) {
      Block air = block;
      while (air.getType() != Material.AIR) {
        air = air.getRelative(BlockFace.UP);
        if (air.getY() > block.getY() + 10) {
          placeFlag(vector.add(new Vector(1, 0, 0)), yaw, true);
          return;
        }
      }
      block = air;

      Block below = block.getRelative(BlockFace.DOWN);
      Block newBlock = block;
      while (below.getType() == Material.AIR) {
        newBlock = below.getRelative(BlockFace.UP);
        below = below.getRelative(BlockFace.DOWN);
        if (below.getY() < 0) {
          this.post.spawn(this, true);
          return;
        }
      }

      block = newBlock;
      if (below.getType() == Material.WATER) {
        below.setType(Material.ICE);
      }

      block.setType(Material.STANDING_BANNER, true);
      Banner banner = (Banner) block.getState();
      banner.setBaseColor(this.baseColor);
      banner.setPatterns(this.patterns);
      org.bukkit.material.Banner bannerData = (org.bukkit.material.Banner) banner.getData();
      bannerData.setFacingDirection(Worlds.toBlockFace(yaw - 180));
      banner.update();
    }

    remove();

    this.currentPlacement = Optional.of(block);
    @Nullable Player dropped = this.carrier.orElse(null);
    this.carrier = Optional.empty();

    if (!getCurrentPost().isPresent()) {
      FlagCountdown countdown = new FlagCountdown(this.match, this.recoverTime, this, this.post);
      this.flagCountdown = Optional.of(countdown);
      Events.call(new FlagDropEvent(this, dropped));
      this.getCm().start(this.flagCountdown.get());
    } else {
      this.flagCountdown.ifPresent(this.getCm()::cancel);
    }
  }

  @Nullable
  @Override
  public DistanceCalculationMetric getDistanceCalculationMetric(Competitor ref) {
    FlagDistanceMetrics metrics = (FlagDistanceMetrics) super.getMetrics();
    if (isCarried()) {
      return metrics.getCarryingMetric();
    } else if (isPermanent() && isCompleted()) {
      return metrics.getPostCompleteMetric();
    } else {
      return metrics.getPreCompleteMetric();
    }
  }

  @Override
  public Iterable<Vector> getDistanceReferenceLocations(Player base) {
    Random random = new Random();
    if (isCarrier(base)) {
      return this.nets.stream()
          .filter(zone -> zone.getRegion() instanceof BoundedRegion && zone.canCapture(base, this))
          .map(zone -> zone.getRegion().getRandomPosition(random)).collect(Collectors.toList());
    } else if (this.getCurrentLocation().isPresent()) {
      return Collections.singleton(this.getCurrentLocation().get().toVector());
    } else {
      return Collections.emptyList();
    }
  }

  public Optional<Location> getCurrentLocation() {
    if (this.carrier.isPresent()) {
      return Optional.of(this.carrier.get().getLocation());
    }
    if (this.currentPlacement.isPresent()) {
      return Optional.of(this.currentPlacement.get().getLocation().add(0.5, 0, 0.5));
    }
    return Optional.empty();
  }

  public boolean isDropped() {
    return this.flagCountdown.isPresent();
  }

  public boolean isCarried() {
    return this.carrier.isPresent();
  }

  public boolean isCarrier(Player player) {
    return this.carrier.equals(Optional.of(player));
  }

  public boolean isCarrier(Team team) {
    boolean found = false;
    for (Player player : team.getPlayers()) {
      if (isCarrier(player)) {
        found = true;
        break;
      }
    }
    return found;
  }

  public Optional<Competitor> getCarrier() {
    if (this.carrier.isPresent()) {
      return this.match.getModule(GroupsModule.class).get().getCompetitorOf(this.carrier.get());
    }
    return Optional.empty();
  }

  /**
   * Transfers the flag to a new player.
   */
  public void setCarrier(Player player) {
    this.flagCountdown.ifPresent(this.getCm()::cancel);

    Competitor competitor = this.match.getModule(GroupsModule.class).get().getCompetitorOf(player)
        .get();

    if (isCarried()) {
      // todo: this shouldn't happen
      this.match.broadcast(new UnlocalizedText("the flag was transferred between players"));
    } else {
      boolean atPost = getCurrentPost().isPresent();

      LocalizedFormat format = Messages.GENERIC_OBJECTIVE_PICKUP;

      if (atPost) {
        format = Messages.GENERIC_OBJECTIVE_TAKEN;
        Events.call(new FlagStealEvent(this, player));
        Events.call(new PlayerEarnPointEvent(player, "flag-steal"));
      } else {
        Events.call(new FlagPickupEvent(this, player));
        Events.call(new PlayerEarnPointEvent(player, "flag-pickup"));
      }

      Localizable flag = this.name.toText(getChatColor());
      Localizable who = new UnlocalizedText(player.getName(), competitor.getChatColor());

      Localizable broadcast = format.with(flag, who);
      broadcast = Messages.UI_IMPORTANT.with(TextStyle.ofBold(), broadcast);

      this.match.broadcast(broadcast);
    }

    remove();

    this.carrier = Optional.of(player);
    this.carrierHelmet = Optional.ofNullable(player.getInventory().getHelmet());

    this.mostRecentCapture = Optional.of(competitor);

    player.getInventory().setHelmet(getItemStack().getItemStack(player));
  }

  public boolean canPickup(Player player, FlagPickupMethod method) {
    if (!this.pickupMethod.allow(method)) {
      return false;
    }
    Optional<PostZone> post = getCurrentPost();
    if (post.isPresent()) {
      return post.get().canPickup(player) && canCarry(player);
    }
    return canCarry(player);
  }

  public void reward() {
    if (!isCarried()) {
      return;
    }

    if (lastReward != null && new Instant().getMillis() - lastReward.getMillis() < 1000) {
      return;
    }

    Duration holding = getHoldingTime().get().toDuration();
    if (getCarryingPointsDelay().isPresent() &&
        getCarryingPointsDelay().get().isLongerThan(holding)) {
      return;
    }

    lastReward = new Instant();

    int points = getCarryingPoints();
    if (getCarryingPointsGrowth().isPresent()) {
      double doubles =
          (double) holding.getMillis() / (double) getCarryingPointsGrowth().get().getMillis();
      while (doubles >= 1) {
        points *= 2;
        doubles -= 1;
      }
    }
    getMatch().getRequiredModule(ObjectivesModule.class).score(getCarrier().get(), points);

  }

  private boolean canCarry(Player player) {
    Competitor competitor = this.match.getModule(GroupsModule.class).get().getCompetitorOf(player)
        .get();
    if (!canComplete(competitor)) {
      return false;
    }
    if (!this.carryCheck.isPresent()) {
      return true;
    }

    CheckContext context = new CheckContext(this.match);
    context.add(new PlayerVariable(player));
    context.add(new LocationVariable(player.getLocation()));
    // todo: FlagVariable?
    return this.carryCheck.get().test(context).passes();
  }

  public Optional<Interval> getHoldingTime() {
    if (this.pickupDate.isPresent()) {
      return Optional.of(new Interval(this.pickupDate.get(), new Instant()));
    }

    return Optional.empty();
  }

  public DyeColor getColor() {
    return this.color.orElse(this.baseColor);
  }

  public ChatColor getChatColor() {
    return Strings.toChatColor(getColor());
  }

  @Override
  public LocalizedXmlString getName() {
    return this.name;
  }

  @Override
  public boolean canComplete(Competitor competitor) {
    return !this.owner.isPresent() || !this.owner.get().equals(competitor);
  }

  @Override
  public boolean isCompleted() {
    return this.carrier.isPresent();
  }

  @Override
  public boolean isCompleted(Competitor competitor) {
    // True IF this is a permanent flag and it has been captured OR the map has a score objective.
    return (isPermanent() && this.mostRecentCapture.isPresent() && this.mostRecentCapture.get()
        .equals(competitor)) || !match.getRequiredModule(ObjectivesModule.class)
        .getObjectivesByType(ScoreObjective.class).isEmpty();
  }

  @Override
  public net.md_5.bungee.api.ChatColor distanceColor(Competitor ref, Player viewer) {
    if (isCarrier(viewer)) {
      return net.md_5.bungee.api.ChatColor.GOLD;
    }
    if (ref instanceof Team) {
      return isCarrier((Team) ref) ? net.md_5.bungee.api.ChatColor.YELLOW
          : super.distanceColor(ref, viewer);
    }
    if (isPermanent() && this.getMostRecentCapture().orElse(null) == ref) {
      return net.md_5.bungee.api.ChatColor.GREEN;
    }
    return net.md_5.bungee.api.ChatColor.GRAY;
  }

  @Override
  public double getCompletion() {
    return isCompleted() ? 1 : 0;
  }

  @Override
  public double getCompletion(Competitor competitor) {
    return isCompleted() ? 1 : 0;
  }

  @Override
  public boolean isIncremental() {
    return false;
  }

  @Override
  public String stringifyDistance(@Nullable Competitor ref, Player viewer, boolean sub) {
    if (isCompleted() && isPermanent() && this.getMetrics().getPostCompleteMetric() == null) {
      return "";
    }

    if (ref == null && isCarried()) {
      return this.getMetrics().getCarryingMetric() == null ? ""
          : super.stringifyDistance(ref, viewer, sub);
    }

    if (!(ref instanceof Team)) {
      return super.stringifyDistance(ref, viewer, sub);
    }

    if (this.isCarrier((Team) ref)) {
      return this.getMetrics().getCarryingMetric() == null ? ""
          : super.stringifyDistance(ref, viewer, sub);
    } else {
      return super.stringifyDistance(ref, viewer, sub);
    }
  }

  @Override
  public ConfigurableField[] getFields() {
    return ArrayUtils.addAll(super.getFields(),
        new OptionalField<>("Carry Check", () -> this.carryCheck, (v) -> this.carryCheck = v, new RegisteredObjectField<>("check", Check.class)),
        new DurationField("Recover Time", () -> this.recoverTime, (v) -> this.recoverTime = v),
        new BooleanField("Permanent", () -> this.permanent, (v) -> this.permanent = v),
        new IntField("Carry Points", () -> this.carryingPoints, (v) -> this.carryingPoints = v),
        new OptionalField<>("Carrying Points Delay", () -> this.carryingPointsDelay, (v) -> carryingPointsDelay = v, new DurationField("carrypoints")),
        new OptionalField<>("Carrying Points Growth", () -> this.carryingPointsGrowth, (v) -> carryingPointsGrowth = v, new DurationField("carrypointsg")),
        new EnumField<>("Pickup Method", () -> this.pickupMethod, (v) -> this.pickupMethod = v, FlagPickupMethod.class)
    );
  }
}