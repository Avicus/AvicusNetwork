package net.avicus.atlas.sets.competitve.objectives.zones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.zones.Zone;
import net.avicus.atlas.module.zones.ZoneMessage;
import net.avicus.atlas.runtimeconfig.fields.ConfigurableField;
import net.avicus.atlas.runtimeconfig.fields.NumberActionField;
import net.avicus.atlas.runtimeconfig.fields.OptionalField;
import net.avicus.atlas.runtimeconfig.fields.RegisteredObjectField;
import net.avicus.atlas.runtimeconfig.fields.SimpleFields.IntField;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.number.NumberAction;
import net.avicus.magma.util.region.Region;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import tc.oc.tracker.event.PlayerCoarseMoveEvent;

@ToString
public class ScoreZone extends Zone {

  private int points;
  private NumberAction scoreModifier;
  private final Optional<Double> pointsGrowth;
  private Optional<Check> check;
  private final Optional<HashMap<Material, HashMap<SingleMaterialMatcher, Integer>>> itemRewards;
  private double nextPoints;

  public ScoreZone(Match match, Region region, Optional<ZoneMessage> message, int points,
      NumberAction scoreModifier, Optional<Double> pointsGrowth, Optional<Check> check,
      Optional<HashMap<Material, HashMap<SingleMaterialMatcher, Integer>>> itemRewards) {
    super(match, region, message);
    this.points = points;
    this.scoreModifier = scoreModifier;
    this.pointsGrowth = pointsGrowth;
    this.check = check;
    this.itemRewards = itemRewards;
    this.nextPoints = points;
  }

  @Override
  public boolean isActive() {
    return this.points > 0;
  }

  public int reward(Match match, Competitor competitor, Player player) {
    int points = (int) Math.floor(this.nextPoints);

    if (this.itemRewards.isPresent()) {
      // Top level generic material in order to easily find in a list of ItemStacks
      // We then retrieve the material matcher in order to narrow down the result scope
      // If the material matcher passes, the points associated with the material are added to the points value.
      HashMap<Material, HashMap<SingleMaterialMatcher, Integer>> rewardStacks = itemRewards.get();
      List<ItemStack> invContents = Arrays.asList(player.getInventory().getContents());
      List<ItemStack> armorContents = Arrays.asList(player.getInventory().getArmorContents());

      // Prevent sneaky players from spreading out
      // items across multiple slots in order to gain more points.
      List<ItemStack> found = new ArrayList<>();

      List<ItemStack> contents = new ArrayList<>();
      contents.addAll(invContents);
      contents.addAll(armorContents);

      for (ItemStack stack : contents) {
        if (stack == null || stack.getType().equals(Material.AIR)) {
          continue;
        }

        if (!rewardStacks.containsKey(stack.getType()) || found.contains(stack)) {
          continue;
        }

        HashMap<SingleMaterialMatcher, Integer> matcherPointMap = rewardStacks.get(stack.getType());

        for (Map.Entry<SingleMaterialMatcher, Integer> entry : matcherPointMap.entrySet()) {
          if (entry.getKey().matches(stack.getType(), stack.getData().getData())) {
            points = points + entry.getValue();
            found.add(stack);
          }
        }
      }
    }

    match.getRequiredModule(ObjectivesModule.class)
        .score(competitor, points, scoreModifier, player);

    if (this.pointsGrowth.isPresent()) {
      this.nextPoints *= this.pointsGrowth.get();
    }
    return points;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onMove(PlayerCoarseMoveEvent event) {
    handle(event.getPlayer(), event.getFrom(), event.getTo());
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onTP(PlayerTeleportEvent event) {
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
      Competitor competitor = match.getRequiredModule(GroupsModule.class).getCompetitorOf(player)
          .orElse(null);

      if (competitor == null) {
        return;
      }

      if (this.check.isPresent()) {
        CheckContext context = new CheckContext(this.match);
        context.add(new PlayerVariable(player));
        context.add(new LocationVariable(toLoc));
        if (this.check.get().test(context).fails()) {
          return;
        }
      }

      int scored = reward(this.match, competitor, player);

      if (this.scoreModifier.equals(NumberAction.ADD)) {
        Localizable points = new LocalizedNumber(scored,
            TextStyle.ofColor(competitor.getChatColor()));
        Localizable name = new UnlocalizedText(player.getName(),
            TextStyle.ofColor(competitor.getChatColor()));

        if (scored == 1) {
          this.match.broadcast(Messages.GENERIC_OBJECTIVE_SCORED.with(points, name));
        } else {
          this.match.broadcast(Messages.GENERIC_OBJECTIVE_SCORED_PLURAL.with(points, name));
        }
      }
    }
  }

  @Override
  public void onFieldChange(String name) {
    super.onFieldChange(name);
    if (name.equalsIgnoreCase("points"))
      this.nextPoints = points;
  }

  @Override
  public ConfigurableField[] getFields() {
    return ArrayUtils.addAll(super.getFields(),
        new IntField("Points", () -> this.points, (v) -> this.points = v),
        new NumberActionField("Score Modifier", () -> this.scoreModifier, (v) -> this.scoreModifier = v),
        new OptionalField<>("Check", () -> this.check, (v) -> this.check = v, new RegisteredObjectField<>("Check", Check.class))
    );
  }

  @Override
  public String getDescription(CommandSender viewer) {
    return "Scorebox" + super.getDescription(viewer);
  }
}
