package net.avicus.atlas.sets.competitve.objectives.wool;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.objectives.GlobalObjective;
import net.avicus.atlas.module.objectives.TouchableDistanceMetrics;
import net.avicus.atlas.module.objectives.TouchableObjective;
import net.avicus.atlas.util.Messages;
import net.avicus.atlas.util.Players;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.inventory.MaterialMatcher;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.LocalizedText;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.utils.Strings;
import net.avicus.magma.util.region.BoundedRegion;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import org.joda.time.Duration;

@Getter
@ToString(exclude = "match")
public class WoolObjective extends TouchableObjective implements GlobalObjective {

  private final Match match;
  private final LocalizedXmlString name;
  private final Optional<Team> team;
  private final DyeColor color;
  private final Optional<BoundedRegion> source;
  private final BoundedRegion destination;
  private final boolean pickup;
  private final boolean refill;
  private final int maxRefill;
  private final Optional<Duration> refillDelay;
  private final boolean craftable;
  private final boolean fireworks;
  private final MaterialMatcher matcher;

  private final Map<Block, Map<Integer, ItemStack>> refills;
  private boolean placed;

  public WoolObjective(Match match,
      TouchableDistanceMetrics metrics,
      Optional<Team> team,
      DyeColor color,
      Optional<BoundedRegion> source,
      BoundedRegion destination,
      boolean pickup,
      boolean refill,
      int maxRefill,
      Optional<Duration> refillDelay,
      boolean craftable,
      boolean fireworks) {
    super(match, metrics);
    this.match = match;
    this.name = new LocalizedXmlString(Messages.forWoolColor(color));
    this.team = team;
    this.color = color;
    this.source = source;
    this.destination = destination;
    this.pickup = pickup;
    this.refill = refill;
    this.maxRefill = maxRefill;
    this.refillDelay = refillDelay;
    this.craftable = craftable;
    this.fireworks = fireworks;

    this.matcher = new SingleMaterialMatcher(Material.WOOL, this.color.getWoolData());
    this.refills = new HashMap<>();
  }

  public boolean canPickup(Group group) {
    return this.pickup || (this.team.isPresent() && !this.team.get().equals(group));
  }

  public ChatColor getChatColor() {
    return Strings.toChatColor(this.color);
  }


  public void place(Player who) {
    Preconditions.checkArgument(!this.placed, "wool already placed");
    this.placed = true;

    if (this.fireworks) {
      spawnFirework();
    }

    Group group = this.match.getRequiredModule(GroupsModule.class).getGroup(who);

    Localizable woolName = this.name.toText(getChatColor());
    UnlocalizedText teamName = new UnlocalizedText(who.getName(), group.getChatColor());

    LocalizedText broadcast = Messages.GENERIC_OBJECTIVE_PLACED.with(woolName, teamName);
    broadcast = Messages.UI_IMPORTANT.with(TextStyle.ofBold(), broadcast);
    this.match.broadcast(broadcast);
  }

  private Firework spawnFirework() {
    Location location = this.destination.getCenter().toLocation(this.match.getWorld());
    Firework firework = (Firework) this.match.getWorld().spawnEntity(location, EntityType.FIREWORK);
    FireworkMeta meta = firework.getFireworkMeta();
    meta.setPower(0);

    FireworkEffect.Builder builder = FireworkEffect.builder();
    builder.with(FireworkEffect.Type.BURST);
    builder.withColor(this.color.getFireworkColor());
    builder.withTrail();

    meta.addEffect(builder.build());
    firework.setFireworkMeta(meta);

    firework.setVelocity(firework.getVelocity().multiply(0.7));

    // 1.8-1.9 Support
    Players.playFireworkSound();

    return firework;
  }

  public boolean isRefillable(Block block) {
    return this.refills.containsKey(block);
  }

  public Optional<Map<Integer, ItemStack>> getRefill(Block block) {
    return Optional.ofNullable(this.refills.get(block));
  }

  @Override
  public void initialize() {
    if (!this.source.isPresent() || !this.refill) {
      return;
    }
    for (Vector vector : this.source.get()) {
      Block block = vector.toLocation(match.getWorld()).getBlock();
      if (!(block.getState() instanceof InventoryHolder)) {
        continue;
      }

      InventoryHolder chest = (InventoryHolder) block.getState();

      Map<Integer, ItemStack> items = new HashMap<>();
      this.refills.put(block, items);
      for (int i = 0; i < chest.getInventory().getSize(); i++) {
        ItemStack item = chest.getInventory().getItem(i);
        if (item != null && this.matcher.matches(item.getData())) {
          items.put(i, chest.getInventory().getItem(i).clone());
        }
      }
    }
  }

  @Override
  public boolean canComplete(Competitor competitor) {
    return !this.team.isPresent() || this.team.get().equals(competitor.getGroup());
  }

  @Override
  public Iterable<Vector> getDistanceReferenceLocations(Player base) {
    if (hasTouchedRecently(base)) {
      return Collections.singleton(this.destination.getCenter());
    } else if (this.source.isPresent()) {
      return Collections.singleton(this.source.get().getCenter());
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public boolean isCompleted(Competitor competitor) {
    return isCompleted();
  }

  @Override
  public double getCompletion(Competitor competitor) {
    return isCompleted(competitor) ? 1.0 : 0;
  }

  @Override
  public boolean isIncremental() {
    return false;
  }

  @Override
  public boolean isCompleted() {
    return isPlaced();
  }

  @Override
  public double getCompletion() {
    if (isCompleted()) {
      return 1.0;
    } else if (isTouched()) {
      return 0.5;
    } else {
      return 0;
    }
  }

  @Override
  public boolean shouldShowDistance(@Nullable Competitor ref, Player viewer) {
    if (!this.source.isPresent() && !isTouched()) {
      return false;
    } else {
      return super.shouldShowDistance(ref, viewer);
    }
  }

  @Override
  public LocalizableFormat getTouchMessage() {
    return Messages.GENERIC_OBJECTIVE_TOUCHED;
  }
}
