package net.avicus.atlas.component.visual;

import net.avicus.atlas.Atlas;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.compendium.settings.Setting;
import net.avicus.compendium.settings.types.SettingTypes;
import net.avicus.magma.module.ListenerModule;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class VisualEffectComponent implements ListenerModule {

  public static final Setting<Boolean> BLOOD_SETTING = new Setting<>(
      "blood",
      SettingTypes.BOOLEAN,
      true,
      Translations.VISUAL_EFFECT_BLOOD_SETTING_NAME.with(),
      Translations.VISUAL_EFFECT_BLOOD_SETTING_DESCRIPTION.with()
  );
  private static final Material BLOOD_MATERIAL = Material.REDSTONE_BLOCK;


  @Override
  public void enable() {
    PlayerSettings.register(BLOOD_SETTING);
  }

  @Override
  public void disable() {
    PlayerSettings.unregister(BLOOD_SETTING);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void renderBloodEffect(final EntityDamageByEntityEvent event) {
    if (event.getDamage() <= 0 || !(event.getEntity() instanceof Player)) {
      return;
    }

    final Player victim = (Player) event.getEntity();

    // don't render blood for invisible victims
    if (victim.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
      return;
    }

    final Location location = victim.getLocation().clone()
        .add(new Vector(0, victim.getEyeHeight() / 2, 0));
    if (location.getY() < 0) {
      return;
    }

    for (final Player player : victim.getWorld().getPlayers()) {
      if (PlayerSettings.get(player, BLOOD_SETTING)) {
        player.playEffect(location, Effect.STEP_SOUND, BLOOD_MATERIAL);
      }
    }
  }

  @EventHandler
  public void onExplodeEntity(EntityExplodeEvent event) {
    showExplosion(event.getLocation());
  }

  @EventHandler
  public void showExplodeBlock(BlockExplodeEvent event) {
    showExplosion(event.getBlock().getLocation());
  }

  private void showExplosion(Location location) {
    Atlas.performOnMatch(m -> {
      m.getWorld().getEntitiesByClass(Player.class).forEach(e -> {
        if (e.getLocation().distanceSquared(location) >= (60 * 60)) {
          e.playEffect(location, Effect.EXPLOSION_LARGE, null);
        }
      });
    });
  }
}
