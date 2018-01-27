package net.avicus.atlas.module.kills;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.util.Messages;
import net.avicus.atlas.util.ScopableItemStack;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.compendium.settings.Setting;
import net.avicus.compendium.settings.types.SettingTypes;
import net.avicus.grave.event.PlayerDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import tc.oc.tracker.Damage;
import tc.oc.tracker.DamageInfo;
import tc.oc.tracker.Lifetime;
import tc.oc.tracker.damage.AnvilDamageInfo;
import tc.oc.tracker.damage.BlockDamageInfo;
import tc.oc.tracker.damage.ExplosiveDamageInfo;
import tc.oc.tracker.damage.FallDamageInfo;
import tc.oc.tracker.damage.GravityDamageInfo;
import tc.oc.tracker.damage.LavaDamageInfo;
import tc.oc.tracker.damage.MeleeDamageInfo;
import tc.oc.tracker.damage.OwnedMobDamageInfo;
import tc.oc.tracker.damage.ProjectileDamageInfo;
import tc.oc.tracker.damage.VoidDamageInfo;
import tc.oc.tracker.trackers.base.gravity.Fall.Cause;
import tc.oc.tracker.trackers.base.gravity.Fall.From;

public class DeathMessage {

  public static final Setting<DeathMessageSetting> SETTING = new Setting<DeathMessageSetting>(
      "death-messages",
      SettingTypes.enumOf(DeathMessageSetting.class),
      DeathMessageSetting.ALL,
      Translations.SETTING_DEATHMESSAGE_NAME.with(),
      Translations.SETTING_DEATHMESSAGE_DESCRIPTION.with()
  );

  /**
   * Creates a localized death message.
   *
   * @param event The death event.
   * @param playersInvolved An empty list, modified through this method to include all players
   * involved in this death message.
   */
  private static Localizable create(Match match, PlayerDeathEvent event,
      List<UUID> playersInvolved) {
    playersInvolved.add(event.getPlayer().getUniqueId());

    Lifetime lifetime = event.getLifetime();
    Damage last = lifetime.getLastDamage();
    Location loc = event.getLocation();

    Player player = event.getPlayer();
    Competitor competitor = match.getRequiredModule(GroupsModule.class).getCompetitorOf(player)
        .orElse(null);
    Localizable name;
    if (competitor == null) {
      name = new UnlocalizedText(player.getDisplayName());
    } else {
      name = new UnlocalizedText(player.getName(), competitor.getChatColor());
    }

    if (last == null) {
      return Messages.DEATH_DIED.with(ChatColor.GRAY, name);
    }

    DamageInfo info = last.getInfo();

    if (info.getResolvedDamager() != null) {
      if (!(info.getResolvedDamager() instanceof Player)) {
        String pretty = pretty(info.getResolvedDamager().getType());
        Localizable entity = new UnlocalizedText(pretty);

        Localizable result = Messages.DEATH_BY_MOB.with(ChatColor.GRAY, name, entity);

        if (info instanceof OwnedMobDamageInfo) {
          OwnedMobDamageInfo owned = (OwnedMobDamageInfo) info;

          if (owned.getMobOwner() != null) {
            Localizable attackerName = new UnlocalizedText(owned.getMobOwner().getDisplayName());
            result = Messages.DEATH_BY_PLAYER_MOB.with(ChatColor.GRAY, name, attackerName, entity);
            playersInvolved.add(owned.getMobOwner().getUniqueId());
          }
        }

        return result;
      }

      Player attacker = (Player) info.getResolvedDamager();
      playersInvolved.add(attacker.getUniqueId());
      Competitor attackerCompetitor = match.getRequiredModule(GroupsModule.class)
          .getCompetitorOf(attacker).orElse(null);
      Localizable attackerName;
      if (attackerCompetitor == null) {
        attackerName = new UnlocalizedText(attacker.getDisplayName());
      } else {
        attackerName = new UnlocalizedText(attacker.getName(), attackerCompetitor.getChatColor());
      }

      if (info instanceof AnvilDamageInfo) {
        return Messages.DEATH_BY_PLAYER_ANVIL.with(ChatColor.GRAY, name, attackerName);
      } else if (info instanceof ExplosiveDamageInfo) {
        return Messages.DEATH_BY_PLAYER_TNT.with(ChatColor.GRAY, name, attackerName);
      } else if (info instanceof GravityDamageInfo) {
        GravityDamageInfo gravity = (GravityDamageInfo) info;

        boolean isVoid = loc.getY() < 0;

        if (gravity.getCause() == Cause.HIT) {
          if (gravity.getFrom() == From.FLOOR) {
            if (isVoid) {
              return Messages.DEATH_HIT_FLOOR_VOID.with(ChatColor.GRAY, name, attackerName);
            } else {
              return Messages.DEATH_HIT_FLOOR_FALL.with(ChatColor.GRAY, name, attackerName);
            }
          } else if (gravity.getFrom() == From.LADDER) {
            if (isVoid) {
              return Messages.DEATH_HIT_LADDER_VOID.with(ChatColor.GRAY, name, attackerName);
            } else {
              return Messages.DEATH_HIT_LADDER_FALL.with(ChatColor.GRAY, name, attackerName);
            }
          } else if (gravity.getFrom() == From.WATER) {
            if (isVoid) {
              return Messages.DEATH_HIT_WATER_VOID.with(ChatColor.GRAY, name, attackerName);
            } else {
              return Messages.DEATH_HIT_WATER_FALL.with(ChatColor.GRAY, name, attackerName);
            }
          }
        } else if (gravity.getCause() == Cause.SHOOT) {
          Double distance = null;

          Damage damage = lifetime.getLastDamage(ProjectileDamageInfo.class);
          if (damage != null) {
            distance = ((ProjectileDamageInfo) damage.getInfo()).getDistance();
          }

          Localizable shotMessage = null;

          if (gravity.getFrom() == From.FLOOR) {
            if (isVoid) {
              shotMessage = Messages.DEATH_SHOT_FLOOR_VOID.with(ChatColor.GRAY, name, attackerName);
            } else {
              shotMessage = Messages.DEATH_SHOT_FLOOR_FALL.with(ChatColor.GRAY, name, attackerName);
            }
          } else if (gravity.getFrom() == From.LADDER) {
            if (isVoid) {
              shotMessage = Messages.DEATH_SHOT_LADDER_VOID
                  .with(ChatColor.GRAY, name, attackerName);
            } else {
              shotMessage = Messages.DEATH_SHOT_LADDER_FALL
                  .with(ChatColor.GRAY, name, attackerName);
            }
          } else if (gravity.getFrom() == From.WATER) {
            if (isVoid) {
              shotMessage = Messages.DEATH_SHOT_WATER_VOID.with(ChatColor.GRAY, name, attackerName);
            } else {
              shotMessage = Messages.DEATH_SHOT_WATER_FALL.with(ChatColor.GRAY, name, attackerName);
            }
          }

          if (distance == null) {
            return shotMessage;
          }

          LocalizableFormat format = new UnlocalizedFormat("{0} {1}");
          Localizable number = new LocalizedNumber(distance, 1, 1);
          number.style().color(bowDistanceColor(distance));

          return format.with(ChatColor.GRAY, shotMessage, Messages.DEATH_BLOCKS.with(number));
        } else if (gravity.getCause() == Cause.SPLEEF) {
          if (gravity.getFrom() == From.FLOOR) {
            if (isVoid) {
              return Messages.DEATH_SPLEEF_FLOOR_VOID.with(ChatColor.GRAY, name, attackerName);
            } else {
              return Messages.DEATH_SPLEEF_FLOOR_FALL.with(ChatColor.GRAY, name, attackerName);
            }
          } else {
            return Messages.DEATH_SPLEEF_BY_PLAYER.with(ChatColor.GRAY, name, attackerName);
          }
        }
      } else if (info instanceof MeleeDamageInfo) {
        MeleeDamageInfo melee = (MeleeDamageInfo) info;

        if (melee.getWeapon() == Material.AIR) {
          return Messages.DEATH_BY_MELEE_FISTS.with(ChatColor.GRAY, name, attackerName);
        } else {
          String pretty = pretty(melee.getWeapon());
          if (melee.getWeaponStack() != null && ScopableItemStack.ALLOW_NAMES
              .get(melee.getWeaponStack())) {
            pretty = melee.getWeaponStack().getItemMeta().getDisplayName();
          }
          Localizable material = new UnlocalizedText(pretty);
          return Messages.DEATH_BY_MELEE.with(ChatColor.GRAY, name, attackerName, material);
        }
      } else if (info instanceof ProjectileDamageInfo) {
        ProjectileDamageInfo projectileInfo = (ProjectileDamageInfo) info;

        String pretty = pretty(projectileInfo.getProjectile().getType());
        Localizable entity = new UnlocalizedText(pretty);

        double distance = last.getLocation().distance(attacker.getLocation());
        Localizable number = new LocalizedNumber(distance, 1, 1);
        number.style().color(bowDistanceColor(distance));

        return Messages.DEATH_BY_PLAYER_PROJECTILE
            .with(ChatColor.GRAY, name, attackerName, entity, number);
      } else if (info instanceof VoidDamageInfo) {
        return Messages.DEATH_BY_PLAYER_VOID.with(ChatColor.GRAY, name, attackerName);
      }
    } else {
      if (info instanceof AnvilDamageInfo) {
        return Messages.DEATH_BY_ANVIL.with(ChatColor.GRAY, name);
      } else if (info instanceof BlockDamageInfo) {
        return Messages.DEATH_BY_BLOCK.with(ChatColor.GRAY, name);
      } else if (info instanceof ExplosiveDamageInfo) {
        return Messages.DEATH_BY_EXPLOSIVE.with(ChatColor.GRAY, name);
      } else if (info instanceof FallDamageInfo) {
        double distance = ((FallDamageInfo) info).getFallDistance();
        Localizable number = new LocalizedNumber(distance, 1, 1);

        return Messages.DEATH_BY_FALL.with(ChatColor.GRAY, name, number);
      } else if (info instanceof LavaDamageInfo) {
        return Messages.DEATH_BY_LAVA.with(ChatColor.GRAY, name);
      } else if (info instanceof VoidDamageInfo) {
        return Messages.DEATH_BY_VOID.with(ChatColor.GRAY, name);
      } else if (info instanceof ProjectileDamageInfo) {
        return Messages.DEATH_BY_PROJECTILE.with(ChatColor.GRAY, name);
      }
    }

    return Messages.DEATH_DIED.with(ChatColor.GRAY, name);
  }

  private static ChatColor bowDistanceColor(double distance) {
    ChatColor color = ChatColor.GRAY;
    if (distance >= 80.0) {
      color = ChatColor.DARK_RED;
    } else if (distance >= 50.0) {
      color = ChatColor.RED;
    } else if (distance >= 35.0) {
      color = ChatColor.GOLD;
    } else if (distance >= 25.0) {
      color = ChatColor.YELLOW;
    } else if (distance >= 10.0) {
      color = ChatColor.GREEN;
    }
    return color;
  }

  private static String pretty(EntityType type) {
    return type.name().toLowerCase().replace('_', ' ');
  }

  private static String pretty(Material type) {
    return type.name().toLowerCase().replace('_', ' ');
  }

  public static void broadcast(Match match, PlayerDeathEvent event) {
    final List<UUID> involved = new ArrayList<>();
    final Localizable genericTranslation = create(match, event, involved);

    for (Player player : match.getPlayers()) {
      final boolean wasInvolved = involved.contains(player.getUniqueId());
      switch (PlayerSettings.get(player, SETTING)) {
        case NONE:
          continue;
        case OWN:
          if (!wasInvolved) {
            continue;
          }
          // fall-through
        case ALL:
          Localizable translation = genericTranslation;
          if (wasInvolved) {
            translation = translation.duplicate();
            translation.style().bold(true);
          }

          player.sendMessage(translation);
      }
    }

    Bukkit.getConsoleSender().sendMessage(genericTranslation);
  }

  public enum DeathMessageSetting {
    ALL,
    OWN,
    NONE;
  }
}
