package net.avicus.atlas.util;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import java.util.UUID;
import me.libraryaddict.disguise.DisguiseAPI;
import net.avicus.atlas.Atlas;
import net.avicus.compendium.snap.SnapClass;
import net.avicus.compendium.snap.SnapConstructor;
import net.avicus.compendium.snap.SnapMethod;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

public class Players {

  public static void setData(Entity entity, String key, Object value) {
    entity.setMetadata(key, new FixedMetadataValue(Atlas.get(), value));
  }

  public static String getDataString(Entity entity, String key, String def) {
    if (!entity.hasMetadata(key)) {
      return def;
    }
    return entity.getMetadata(key).get(0).asString();
  }

  public static boolean getDataBoolean(Entity entity, String key, boolean def) {
    if (!entity.hasMetadata(key)) {
      return def;
    }
    return entity.getMetadata(key).get(0).asBoolean();
  }

  public static void playFireworkSound() {
    String soundName = "FIREWORK_BLAST";

    // 1.8-1.9 Support
    if (VersionUtil.isCombatUpdate()) {
      soundName = "ENTITY_FIREWORK_BLAST";
    }

    for (Player player : Bukkit.getOnlinePlayers()) {
      player.playSound(player.getLocation(), Sound.valueOf(soundName), 1.0F, 1.0F);
    }
  }

  public static void reset(Player player) {
    player.setGameMode(GameMode.SURVIVAL);
    player.setAllowFlight(false);
    player.setFlying(false);
    player.setFlySpeed(0.1F);
    player.setWalkSpeed(0.2F);
    player.setExp(0);
    player.setTotalExperience(0);
    player.setLevel(0);
    player.setMaxHealth(20);
    player.setHealth(20);
    player.setFoodLevel(20);
    player.setSaturation(5);
    player.setCanPickupItems(true);
    player.setFireTicks(0);
    player.setRemainingAir(20);
    player.setFallDistance(0);
    player.setVelocity(new Vector());
    player.resetPlayerWeather();
    player.resetPlayerTime();
    player.setItemOnCursor(null);

    player.eject();
    if (player.getVehicle() != null) {
      player.getVehicle().eject();
    }

    // Remove arrows stuck in player
    WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(player);
    watcher.setObject(9, (byte) 0, true);

    // 1.8-1.9 Start
    if (VersionUtil.isCombatUpdate()) {
      SnapClass attribute = new SnapClass("org.bukkit.attribute.Attribute");
      SnapClass modifier = new SnapClass("org.bukkit.attribute.AttributeModifier");
      SnapClass operation = new SnapClass("org.bukkit.attribute.AttributeModifier$Operation");
      SnapClass attributeInstance = new SnapClass("org.bukkit.attribute.AttributeInstance");
      SnapMethod getAttribute = new SnapClass(player.getClass())
          .getMethod("getAttribute", attribute.getClazz());

      Object genericAttackSpeed = attribute.getField("GENERIC_ATTACK_SPEED").getStatic();
      Object addScalar = operation.getMethod("valueOf", String.class).getStatic("ADD_SCALAR");

      SnapConstructor modifierConstructor = modifier
          .getConstructor(UUID.class, String.class, double.class, addScalar.getClass());

      Object modifierInstance = modifierConstructor
          .newInstance(UUID.randomUUID(), "generic.attackSpeed", 5.001D, addScalar);

      SnapMethod addModifier = attributeInstance
          .getMethod("addModifier", modifierInstance.getClass());
      addModifier.get(getAttribute.get(player, genericAttackSpeed), modifierInstance);

      // 1.9 Version
      // AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", 5.001D, AttributeModifier.Operation.ADD_SCALAR)
      // player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).addModifier(modifier);
    }

    // Inventory
    player.closeInventory();
    player.getInventory().clear();
    player.getInventory().setArmorContents(null);
    player.updateInventory();
    for (PotionEffect effect : player.getActivePotionEffects()) {
      player.removePotionEffect(effect.getType());
    }

    // Spigot
    player.spigot().setCollidesWithEntities(true);
    // Todo?
    if (!VersionUtil.isCombatUpdate()) {
      player.spigot().setAffectsSpawning(true);
    }

    // Disguise
    DisguiseAPI.undisguiseToAll(player);
  }
}