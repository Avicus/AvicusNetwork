package net.avicus.atlas.module.chests.generator;

import com.sk89q.minecraft.util.commands.ChatColor;
import net.avicus.atlas.module.chests.ChestsModule;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.StringUtil;
import net.avicus.compendium.countdown.Countdown;
import net.avicus.compendium.locale.text.Localizable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.joda.time.Duration;

/**
 * Task used to deolay the generator of containers and show a visual clock above them.
 */
public class ChestRegenerateTask extends Countdown {

  /**
   * Module to pull generation data from.
   */
  private final ChestsModule module;
  /**
   * Block that represents the container is being regenerated.
   */
  private final Block block;
  /**
   * Material of the block that represents the container is being regenerated.
   */
  private final Material material;
  /**
   * Armor stand used to display the clock.
   */
  private ArmorStand stand;

  /**
   * Constructor.
   *
   * @param module module to pull generation data from
   * @param block block that represents the container is being regenerated
   * @param delay material of the block that represents the container is being regenerated
   */
  public ChestRegenerateTask(ChestsModule module, Block block, Duration delay) {
    super(delay);
    this.module = module;
    this.block = block;
    this.material = block.getType();
  }

  @Override
  public Localizable getName() {
    return Messages.GENERIC_COUNTDOWN_CHEST_REGENERATE_NAME.with();
  }

  @Override
  public void onStart() {
    final Location location = this.block.getLocation().add(0.5, 0.8, 0.5);
    this.stand = location.getWorld().spawn(location, ArmorStand.class);
    this.stand.setGravity(false);
    this.stand.setSmall(true);
    this.stand.setMarker(true);
    this.stand.setVisible(false);
  }

  /**
   * Update armor stand if the block is still present and the material still matches.
   *
   * @param elapsedTime The amount of time elapsed.
   * @param remainingTime The amount of time remaining in the countdown.
   */
  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    if (this.block.getType() != this.material) {
      this.removeStand();
      return;
    }

    String clock = StringUtil.secondsToClock((int) remainingTime.getStandardSeconds());
    this.stand.setCustomName(ChatColor.GREEN + ChatColor.BOLD.toString() + clock);
    this.stand.setCustomNameVisible(true);
  }

  /**
   * Remove the stand and allow population to begin.
   */
  @Override
  protected void onEnd() {
    this.removeStand();
    this.module.clearGenerated(this.block);
  }

  @Override
  protected void onCancel() {
    this.removeStand();
  }

  private void removeStand() {
    this.stand.remove();
  }
}
