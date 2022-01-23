package net.avicus.atlas.module.zones.zones.filtered;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.zones.Zone;
import net.avicus.atlas.module.zones.ZoneMessage;
import net.avicus.atlas.runtimeconfig.fields.ConfigurableField;
import net.avicus.atlas.runtimeconfig.fields.EnumField;
import net.avicus.atlas.runtimeconfig.fields.OptionalField;
import net.avicus.magma.util.region.Region;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockFromToEvent;

@ToString(callSuper = true)
public class FilteredLiquidZone extends Zone {

  private Optional<LiquidRule> waterRule;
  private Optional<LiquidRule> lavaRule;

  public FilteredLiquidZone(Match match, Region region, Optional<ZoneMessage> message,
      Optional<LiquidRule> waterRule, Optional<LiquidRule> lavaRule) {
    super(match, region, message);
    this.waterRule = waterRule;
    this.lavaRule = lavaRule;
  }

  @Override
  public boolean isActive() {
    return this.waterRule.isPresent() || this.lavaRule.isPresent();
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onBlockChange(BlockFromToEvent event) {
    Material changeMat = event.getBlock().getType();
    boolean water = changeMat == Material.WATER || changeMat == Material.STATIONARY_WATER;
    boolean lava = changeMat == Material.LAVA || changeMat == Material.STATIONARY_LAVA;
    boolean from = getRegion().contains(event.getBlock());
    boolean to = getRegion().contains(event.getToBlock());
    boolean enter = !from && to;
    boolean exit = from && !to;

    if (!(exit || enter) || !(water || lava)) {
      return;
    }

    boolean cancel = false;

    if (water && waterRule.isPresent()) {
      if (enter && this.waterRule.get() != LiquidRule.ONLY_ENTER) {
        cancel = true;
      } else if (exit && this.waterRule.get() != LiquidRule.ONLY_EXIT) {
        cancel = true;
      }
    }

    if (lava && lavaRule.isPresent()) {
      if (enter && this.lavaRule.get() != LiquidRule.ONLY_ENTER) {
        cancel = true;
      } else if (exit && this.lavaRule.get() != LiquidRule.ONLY_EXIT) {
        cancel = true;
      }
    }

    event.setCancelled(cancel);
  }

  @Override
  public String getDescription(CommandSender viewer) {
    return "Liquid Filter" + super.getDescription(viewer);
  }

  @Override
  public ConfigurableField[] getFields() {
    return ArrayUtils.addAll(super.getFields(),
        new OptionalField<>("Water", () -> this.waterRule, (v) -> this.waterRule = v, new EnumField<>("water", LiquidRule.class)),
        new OptionalField<>("Lava", () -> this.lavaRule, (v) -> this.lavaRule = v, new EnumField<>("lava", LiquidRule.class))
        );
  }

  public enum LiquidRule {
    ONLY_ENTER,
    ONLY_EXIT,
    NO_FLOW
  }
}
