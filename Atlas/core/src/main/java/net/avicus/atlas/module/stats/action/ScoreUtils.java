package net.avicus.atlas.module.stats.action;

import static net.avicus.compendium.MathUtil.isInRange;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import java.util.Arrays;
import java.util.HashMap;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tc.oc.tracker.Damage;
import tc.oc.tracker.DamageInfo;
import tc.oc.tracker.Lifetime;
import tc.oc.tracker.damage.AnvilDamageInfo;
import tc.oc.tracker.damage.DispensedProjectileDamageInfo;
import tc.oc.tracker.damage.ExplosiveDamageInfo;
import tc.oc.tracker.damage.GravityDamageInfo;
import tc.oc.tracker.damage.MeleeDamageInfo;
import tc.oc.tracker.damage.ProjectileDamageInfo;
import tc.oc.tracker.trackers.base.gravity.Fall;

/**
 * Various utility methods to get the score a player should receive based on actions and variables.
 * Scores show go from easy -> hard in that the harder it would be to complete an action, the higher
 * the score should be.
 */
public class ScoreUtils {

  public static Pair<StringBuilder, HashMap<Integer, AtomicDouble>> getNearbyPlayers(
      Location location, Competitor own, GroupsModule module) {
    Pair<StringBuilder, HashMap<Integer, AtomicDouble>> res = MutablePair
        .of(new StringBuilder("Nearby Players: "), Maps.newHashMap());
    res.getValue().put(5, new AtomicDouble());
    res.getValue().put(10, new AtomicDouble());
    res.getValue().put(15, new AtomicDouble());
    res.getValue().put(20, new AtomicDouble());
    location.getWorld().getEntitiesByClass(Player.class).forEach(e -> {
      Competitor other = module.getCompetitorOf(e).orElse(null);
      if (other == null || other.equals(own)) {
        return;
      }

      double dist = location.distanceSquared(e.getLocation());
      int toIncrement = 0;
      if (dist <= 5) {
        toIncrement = 5;
      } else if (isInRange(dist, 5, 10, true)) {
        toIncrement = 10;
      } else if (isInRange(dist, 10, 15, true)) {
        toIncrement = 15;
      } else if (isInRange(dist, 15, 20, true)) {
        toIncrement = 20;
      } else {
        return;
      }

      Pair<StringBuilder, Double> toughnessScore = ScoreUtils.getEntityToughnessScore(e);
      res.getValue().get(toIncrement).addAndGet(toughnessScore.getValue());
      res.getKey().append(
          "toughness data for entity " + dist + "blocks away: " + toughnessScore.getKey().toString()
              + " ");
    });
    return res;
  }

  // TODO: enchantments/attributes
  public static Pair<StringBuilder, Double> getItemScore(ItemStack item) {
    Pair<StringBuilder, Double> res = MutablePair.of(new StringBuilder("Item Stack Score: "), 0.0);
    if (item == null) {
      res.getKey().append("N/A (1.0)");
      res.setValue(1.0);
    } else {
      double d;
      res.getKey().append("type=" + item.getType() + " ");
      switch (item.getType()) {
        case DIAMOND_SWORD:
          d = .13;
          break;
        case DIAMOND_AXE:
          d = .1;
          break;
        case IRON_AXE:
          d = .45;
          break;
        case IRON_SWORD:
          d = .4;
          break;
        case STONE_AXE:
          d = .55;
          break;
        case STONE_SWORD:
          d = .6;
          break;
        case WOOD_AXE:
          d = .8;
          break;
        case WOOD_SWORD:
          d = .9;
          break;
        case LEATHER_BOOTS:
        case LEATHER_CHESTPLATE:
        case LEATHER_HELMET:
        case LEATHER_LEGGINGS:
          d = .8;
          break;
        case IRON_BOOTS:
        case IRON_CHESTPLATE:
        case IRON_LEGGINGS:
        case IRON_HELMET:
          d = .54;
          break;
        case CHAINMAIL_BOOTS:
        case CHAINMAIL_CHESTPLATE:
        case CHAINMAIL_HELMET:
        case CHAINMAIL_LEGGINGS:
          d = .65;
          break;
        case DIAMOND_BOOTS:
        case DIAMOND_CHESTPLATE:
        case DIAMOND_HELMET:
        case DIAMOND_LEGGINGS:
          d = .21;
          break;
        default:
          d = 1.0;
      }
      res.setValue(d);
      res.getKey().append("score=" + res.getValue());
    }
    return res;
  }

  public static Pair<StringBuilder, Double> getProjectileScore(Projectile projectile) {
    double res = 0;
    StringBuilder stringBuilder = new StringBuilder("Projectile Score: ");
    switch (projectile.getType()) {
      case ARROW:
        res = ((Arrow) projectile).spigot().getDamage() / 2;
        break;
      case EGG:
        res = 0.8;
        break;
      case WITHER_SKULL:
        res = 0.12;
        break;
      case SNOWBALL:
        res = 0.8;
        break;
      case ENDER_PEARL:
        res = 0.6;
        break;
      case FIREBALL:
        res = 0.4;
        break;
      case SMALL_FIREBALL:
        res = 0.5;
        break;
      case SPLASH_POTION:
        stringBuilder.append("Splash Effects: ");
        for (PotionEffect effect : ((ThrownPotion) projectile).getEffects()) {
          Pair<StringBuilder, Double> effectScore = getEffectScore(effect);
          stringBuilder.append(effectScore.getKey().toString() + " ");
          res = res + effectScore.getValue();
        }
        break;
      default:
        res = 0;
    }
    stringBuilder.append("type=" + projectile.getType() + " score=" + res + " ");
    return MutablePair.of(stringBuilder, res);
  }

  public static Pair<StringBuilder, Double> getEffectScore(PotionEffect effect) {
    double res = (effect.getAmplifier() * .7) + (effect.getDuration() / 2) * .3;
    PotionEffectType type = effect.getType();
    if (type.equals(PotionEffectType.SPEED)) {
      res = res + 1.3;
    }
    if (type.equals(PotionEffectType.SLOW)) {
      res = res - 0.5;
    }
    if (type.equals(PotionEffectType.FAST_DIGGING)) {
      res = res + 1.3;
    }
    if (type.equals(PotionEffectType.SLOW_DIGGING)) {
      res = res + 0.7;
    }
    if (type.equals(PotionEffectType.INCREASE_DAMAGE)) {
      res = res + 0.2;
    }
    if (type.equals(PotionEffectType.HEAL)) {
      res = res + 2.3;
    }
    if (type.equals(PotionEffectType.HARM)) {
      res = res - 0.2;
    }
    if (type.equals(PotionEffectType.JUMP)) {
      res = res + 1.3;
    }
    if (type.equals(PotionEffectType.CONFUSION)) {
      res = res + 0.5;
    }
    if (type.equals(PotionEffectType.REGENERATION)) {
      res = res + 2.3;
    }
    if (type.equals(PotionEffectType.DAMAGE_RESISTANCE)) {
      res = res + 2.7;
    }
    if (type.equals(PotionEffectType.FIRE_RESISTANCE)) {
      res = res + 1.5;
    }
    if (type.equals(PotionEffectType.WATER_BREATHING)) {
      res = res + 1.5;
    }
    if (type.equals(PotionEffectType.INVISIBILITY)) {
      res = res + 4.5;
    }
    if (type.equals(PotionEffectType.BLINDNESS)) {
      res = res + 0.7;
    }
    if (type.equals(PotionEffectType.NIGHT_VISION)) {
      res = res - 0.4;
    }
    if (type.equals(PotionEffectType.HUNGER)) {
      res = res + 0.9;
    }
    if (type.equals(PotionEffectType.WEAKNESS)) {
      res = res + 0.3;
    }
    if (type.equals(PotionEffectType.POISON)) {
      res = res + 0.2;
    }
    if (type.equals(PotionEffectType.WITHER)) {
      res = res + 0.06;
    }
    if (type.equals(PotionEffectType.HEALTH_BOOST)) {
      res = res + 2.3;
    }
    if (type.equals(PotionEffectType.ABSORPTION)) {
      res = res + 1.8;
    }
    if (type.equals(PotionEffectType.SATURATION)) {
      res = res + 1.2;
    }
    return MutablePair.of(new StringBuilder("type=" + type.getName() + " score=" + res + " "), res);
  }

  public static Pair<StringBuilder, Double> getEquipmentScore(EntityEquipment equipment) {
    Pair<StringBuilder, Double> res = MutablePair.of(new StringBuilder(), 0.0);
    res.getKey().append("Equipment Score: ");
    if (equipment == null) {
      res.getKey().append("N/A");
      return res;
    }
    res.getKey().append("Armor: ");
    Arrays.stream(equipment.getArmorContents()).forEach(c -> {
      Pair<StringBuilder, Double> item = getItemScore(c);
      res.setValue(res.getValue() + 1.0 - item.getValue());
      res.getKey().append(res.getKey().toString());
    });
    res.getKey().append("In Hand: ");
    Pair<StringBuilder, Double> item = getItemScore(equipment.getItemInHand());
    res.setValue(res.getValue() + 1.0 - item.getValue());
    res.getKey().append(res.getKey().toString());
    res.getKey().append("score=" + res.getValue());
    return res;
  }

  public static Pair<StringBuilder, Double> getEntityToughnessScore(LivingEntity entity) {
    Pair<StringBuilder, Double> res = MutablePair.of(new StringBuilder(), 0.0);
    res.getKey().append("Entity Toughness: Effects: ");
    entity.getActivePotionEffects().forEach(e -> {
      Pair<StringBuilder, Double> score = getEffectScore(e);
      res.setValue(res.getValue() + score.getValue());
      res.getKey().append(score.getKey().toString());
    });
    Pair<StringBuilder, Double> eq = getEquipmentScore(entity.getEquipment());
    res.setValue(res.getValue() + eq.getValue());
    res.getKey().append("Equipment: " + eq.getKey().toString());
    res.setValue(res.getValue() + entity.getHealth() / 5.13);
    res.getKey().append("health=" + entity.getHealth() + " ");
    res.getKey().append("score=" + res.getValue());
    return res;
  }

  public static Pair<StringBuilder, Double> getDamageInfoScore(Lifetime lifetime, DamageInfo info,
      Location damageLoc) {
    Pair<StringBuilder, Double> res = MutablePair.of(new StringBuilder(), 0.0);
    res.getKey().append("DamageType: ");
    if (info instanceof AnvilDamageInfo) {
      AnvilDamageInfo anvil = (AnvilDamageInfo) info;
      Location start = anvil.getAnvil().getSourceLoc();
      Location end = anvil.getAnvil().getLocation();
      double yDiff = start.getY() - end.getY();
      res.setValue((yDiff / 2) * .5);
      res.getKey().append("Anvil yDiff=" + yDiff);
    } else if (info instanceof DispensedProjectileDamageInfo) {
      DispensedProjectileDamageInfo dispensed = (DispensedProjectileDamageInfo) info;
      res.getKey().append("Dispenser ");
      if (dispensed.getDistance() != null) {
        res.setValue(0.4 * dispensed.getDistance());
        res.getKey().append("distance=" + dispensed.getDistance() + " ");
      }
      Pair<StringBuilder, Double> proj = getProjectileScore(dispensed.getProjectile());
      res.setValue(res.getValue() + proj.getValue());
      res.getKey().append("projectile=" + proj.getKey().toString());
    } else if (info instanceof ExplosiveDamageInfo) {
      double dist = ((ExplosiveDamageInfo) info).getExplosive().getLocation()
          .distanceSquared(damageLoc);
      res.setValue(dist / 4);
      res.getKey().append("Explosive distance=" + dist);
    } else if (info instanceof GravityDamageInfo) {
      GravityDamageInfo gravity = (GravityDamageInfo) info;
      res.getKey().append("Gravity ");

      double fallDist = 0;
      if (gravity.getFallLocation() != null) {
        fallDist = gravity.getFallLocation().getY() - damageLoc.getY();
      }
      res.getKey().append("fallDist=" + fallDist + " ");
      fallDist = Math.min(Math.max(0, fallDist), 255);
      double causeMultiple = 0;

      boolean isVoid = damageLoc.getY() < 0;

      res.getKey().append("inVoid=" + isVoid + " ");

      if (gravity.getCause() == Fall.Cause.HIT) {
        res.getKey().append("cause=hit ");
        if (gravity.getFrom() == Fall.From.FLOOR) {
          res.getKey().append("from=floor ");
          if (isVoid) {
            causeMultiple = 0.3;
          } else {
            causeMultiple = 0.9;
          }
        } else if (gravity.getFrom() == Fall.From.LADDER) {
          res.getKey().append("from=laddar ");
          if (isVoid) {
            causeMultiple = 1.2;
          } else {
            causeMultiple = 1.7;
          }
        } else if (gravity.getFrom() == Fall.From.WATER) {
          res.getKey().append("from=water ");
          if (isVoid) {
            causeMultiple = 2.1;
          } else {
            causeMultiple = 2.4;
          }
        }
      } else if (gravity.getCause() == Fall.Cause.SHOOT) {
        Double distance = 0.0;
        res.getKey().append("cause=shoot ");

        Damage damage = lifetime.getLastDamage(ProjectileDamageInfo.class);
        if (damage != null) {
          distance = ((ProjectileDamageInfo) damage.getInfo()).getDistance();
        }

        distance = distance == null ? 0.0 : distance;
        res.getKey().append("distance=" + distance + " ");

        if (gravity.getFrom() == Fall.From.FLOOR) {
          res.getKey().append("from=floor ");
          if (isVoid) {
            causeMultiple = 1.9 * distance;
          } else {
            causeMultiple = 2.2 * distance;
          }
        } else if (gravity.getFrom() == Fall.From.LADDER) {
          res.getKey().append("from=ladder ");
          if (isVoid) {
            causeMultiple = 3.2 * distance;
          } else {
            causeMultiple = 3.5 * distance;
          }
        } else if (gravity.getFrom() == Fall.From.WATER) {
          res.getKey().append("from=water ");
          if (isVoid) {
            causeMultiple = 4.1 * distance;
          } else {
            causeMultiple = 4.4 * distance;
          }
        }
      } else if (gravity.getCause() == Fall.Cause.SPLEEF) {
        res.getKey().append("cause=spleef ");
        if (gravity.getFrom() == Fall.From.FLOOR) {
          res.getKey().append("from=floor ");
          if (isVoid) {
            causeMultiple = 5.1;
          } else {
            causeMultiple = 6.3;
          }
        } else {
          causeMultiple = 4.3;
        }
      }

      res.setValue(((20 - (fallDist / 15)) / 10) * causeMultiple);
    } else if (info instanceof MeleeDamageInfo) {
      MeleeDamageInfo melee = (MeleeDamageInfo) info;
      if (melee.getWeapon() == Material.AIR) {
        res.setValue(2.5);
        res.getKey().append("weapon=fist ");
      } else {
        res.getKey().append("weapon=" + melee.getWeapon() + " ");
        Pair<StringBuilder, Double> weapon = getItemScore(melee.getWeaponStack());
        res.setValue(weapon.getValue());
        res.getKey().append(weapon.getKey());
      }
    }
    res.getKey().append("score=" + res.getValue());
    return res;
  }
}
