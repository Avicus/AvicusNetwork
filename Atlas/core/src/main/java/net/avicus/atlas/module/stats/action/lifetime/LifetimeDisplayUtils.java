package net.avicus.atlas.module.stats.action.lifetime;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multiset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.stats.action.base.PlayerAction;
import net.avicus.atlas.module.stats.action.damage.PlayerAssistKillAction;
import net.avicus.atlas.module.stats.action.damage.PlayerDeathByNaturalAction;
import net.avicus.atlas.module.stats.action.damage.PlayerDeathByPlayerAction;
import net.avicus.atlas.module.stats.action.damage.PlayerDeathBySelfAction;
import net.avicus.atlas.module.stats.action.damage.PlayerKillAction;
import net.avicus.atlas.module.stats.action.lifetime.type.PlayerLifetime;
import net.avicus.atlas.module.stats.action.objective.ObjectiveAction;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedFormat;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.util.CollectionUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tc.oc.tracker.damage.FallDamageInfo;
import tc.oc.tracker.damage.MeleeDamageInfo;
import tc.oc.tracker.damage.ProjectileDamageInfo;

public class LifetimeDisplayUtils {

  private static UnlocalizedFormat format = new UnlocalizedFormat("{0}: {1}");
  private static TextStyle numStyle = TextStyle.ofColor(ChatColor.GOLD).bold();

  // Taglines
  private static List<LocalizedFormat> fallTags = Arrays
      .asList(Translations.STATS_FACTS_FALL_TAGLINE_TAG1,
          Translations.STATS_FACTS_FALL_TAGLINE_TAG2, Translations.STATS_FACTS_FALL_TAGLINE_TAG3);
  private static List<LocalizedFormat> killMostTags = Arrays
      .asList(Translations.STATS_FACTS_KILLS_MOST_TAGLINE_TAG1,
          Translations.STATS_FACTS_KILLS_MOST_TAGLINE_TAG2,
          Translations.STATS_FACTS_KILLS_MOST_TAGLINE_TAG3,
          Translations.STATS_FACTS_KILLS_MOST_TAGLINE_TAG4);
  private static List<LocalizedFormat> killAssistTags = Arrays
      .asList(Translations.STATS_FACTS_KILLS_ASSISTS_TAGLINE_TAG1,
          Translations.STATS_FACTS_KILLS_ASSISTS_TAGLINE_TAG2,
          Translations.STATS_FACTS_KILLS_ASSISTS_TAGLINE_TAG3);
  private static List<LocalizedFormat> snipeTags = Arrays
      .asList(Translations.STATS_FACTS_SNIPE_TAGLINE_TAG1,
          Translations.STATS_FACTS_SNIPE_TAGLINE_TAG2, Translations.STATS_FACTS_SNIPE_TAGLINE_TAG3,
          Translations.STATS_FACTS_SNIPE_TAGLINE_TAG4);

  private static Random random = new Random();

  public static Localizable randomTag(List<LocalizedFormat> source) {
    return source.get(random.nextInt(source.size())).with(ChatColor.BLUE);
  }

  public static List<Localizable> getMeleeDisplay(PlayerLifetime lifetime) {
    return getMeleeDisplay(lifetime.getActions());
  }

  public static List<Localizable> getMeleeDisplay(LifetimeStore store, UUID uuid) {
    return getMeleeDisplay(store.getPlayerLifetimes().get(uuid).stream()
        .flatMap(lifetime -> lifetime.getActions().stream()).collect(Collectors.toList()));
  }

  public static List<Localizable> getMeleeDisplay(List<PlayerAction> actions) {
    List<Localizable> result = new ArrayList<>();

    int kills = (int) actions.stream()
        .filter(playerAction -> playerAction instanceof PlayerKillAction).count();
    if (kills > 0) {
      result.add(format
          .with(ChatColor.AQUA, Translations.STATS_DAMAGE_KILLS_KILLS.with(ChatColor.AQUA),
              new LocalizedNumber(kills, numStyle)));

      if (kills > 4) {
        // Weapon
        Material weapon = CollectionUtils
            .mostCommonAttribute(actions, PlayerKillAction.class, (playerAction -> {
              PlayerKillAction action = (PlayerKillAction) playerAction;
              if (action.getInfo() instanceof MeleeDamageInfo) {
                return ((MeleeDamageInfo) action.getInfo()).getWeapon();
              }
              return null;
            }));
        if (weapon != null && weapon != Material.AIR) {
          result.add(Translations.STATS_DAMAGE_KILLS_CAUSE_WEAPON
              .with(ChatColor.AQUA, weapon.name().replace("_", " ").toLowerCase()));
        }

        // Bow
        Number distance = CollectionUtils
            .highestNumber(actions, PlayerKillAction.class, (playerAction -> {
              PlayerKillAction action = (PlayerKillAction) playerAction;
              if (action.getInfo() instanceof ProjectileDamageInfo) {
                return ((ProjectileDamageInfo) action.getInfo()).getDistance();
              }
              return 0.0;
            }));
        if (distance != null && distance.intValue() > 10) {
          result.add(Translations.STATS_DAMAGE_KILLS_CAUSE_BOW
              .with(ChatColor.AQUA, new LocalizedNumber(distance, 1, 1, numStyle)));
        }
      }

      if (kills > 15) {
        Player victim = CollectionUtils
            .mostCommonAttribute(actions, PlayerKillAction.class, (playerAction -> {
              PlayerKillAction action = (PlayerKillAction) playerAction;
              return action.getVictim();
            }));
        if (victim != null) {
          int count = (int) actions.stream().filter(
              playerAction -> playerAction instanceof PlayerKillAction
                  && ((PlayerKillAction) playerAction).getVictim().equals(victim)).count();
          result.add(Translations.STATS_DAMAGE_KILLS_MOST
              .with(ChatColor.AQUA, new UnlocalizedText(victim.getDisplayName()),
                  new LocalizedNumber(count, numStyle)));
        }
      }
    }

    int killAssists = (int) actions.stream()
        .filter(playerAction -> playerAction instanceof PlayerAssistKillAction).count();
    if (killAssists > 0) {
      // Weapon
      Material weapon = CollectionUtils
          .mostCommonAttribute(actions, PlayerAssistKillAction.class, (playerAction -> {
            PlayerAssistKillAction action = (PlayerAssistKillAction) playerAction;
            if (action.getInfo() instanceof MeleeDamageInfo) {
              return ((MeleeDamageInfo) action.getInfo()).getWeapon();
            }
            return null;
          }));
      result.add(Translations.STATS_DAMAGE_ASSIST_ASSISTS
          .with(ChatColor.AQUA, new LocalizedNumber(killAssists, numStyle)));
      if (weapon != null && weapon != Material.AIR) {
        result.add(Translations.STATS_DAMAGE_ASSIST_WEAPON
            .with(ChatColor.AQUA, weapon.name().replace("_", " ").toLowerCase()));
      }
    }

    int deathsNatural = (int) actions.stream()
        .filter(playerAction -> playerAction instanceof PlayerDeathByNaturalAction).count();
    if (deathsNatural > 0) {
      result.add(format.with(ChatColor.AQUA,
          Translations.STATS_DAMAGE_DEATHS_CATEGORY_NATURAL.with(ChatColor.AQUA),
          new LocalizedNumber(deathsNatural, numStyle)));
      // Fall
      Number totalFall = actions.stream()
          .filter(playerAction -> playerAction instanceof PlayerDeathByNaturalAction)
          .mapToDouble(value -> {
            PlayerDeathByNaturalAction action = (PlayerDeathByNaturalAction) value;
            if (action.getInfo() instanceof FallDamageInfo) {
              return (double) ((FallDamageInfo) action.getInfo()).getFallDistance();
            }

            return 0.0;
          }).sum();
      Number highestFall = CollectionUtils
          .highestNumber(actions, PlayerDeathByNaturalAction.class, value -> {
            PlayerDeathByNaturalAction action = (PlayerDeathByNaturalAction) value;
            if (action.getInfo() instanceof FallDamageInfo) {
              return (double) ((FallDamageInfo) action.getInfo()).getFallDistance();
            }

            return 0.0;
          });
      if (totalFall.intValue() > 20) {
        result.add(Translations.STATS_DAMAGE_DEATHS_CAUSE_FALL_TOTAL
            .with(ChatColor.AQUA, new LocalizedNumber(totalFall, numStyle)));
      }
      if (highestFall != null && highestFall.intValue() > 30) {
        result.add(Translations.STATS_DAMAGE_DEATHS_CAUSE_FALL_MOST
            .with(ChatColor.AQUA, new LocalizedNumber(highestFall, numStyle)));
      }
    }

    int deathsSelf = (int) actions.stream()
        .filter(playerAction -> playerAction instanceof PlayerDeathBySelfAction).count();
    if (deathsSelf > 0) {
      result.add(format
          .with(ChatColor.AQUA, Translations.STATS_DAMAGE_DEATHS_CATEGORY_SELF.with(ChatColor.AQUA),
              new LocalizedNumber(deathsSelf, numStyle)));
    }

    int deathsPlayer = (int) actions.stream()
        .filter(playerAction -> playerAction instanceof PlayerDeathByPlayerAction).count();
    if (deathsPlayer > 0) {
      // Bow
      Number distance = CollectionUtils
          .highestNumber(actions, PlayerDeathByPlayerAction.class, (playerAction -> {
            PlayerDeathByPlayerAction action = (PlayerDeathByPlayerAction) playerAction;
            if (action.getInfo() instanceof ProjectileDamageInfo) {
              return ((ProjectileDamageInfo) action.getInfo()).getDistance();
            }
            return 0.0;
          }));
      // Weapon
      Material weapon = CollectionUtils
          .mostCommonAttribute(actions, PlayerDeathByPlayerAction.class, (playerAction -> {
            PlayerDeathByPlayerAction action = (PlayerDeathByPlayerAction) playerAction;
            if (action.getInfo() instanceof MeleeDamageInfo) {
              return ((MeleeDamageInfo) action.getInfo()).getWeapon();
            }
            return null;
          }));
      result.add(format.with(ChatColor.AQUA,
          Translations.STATS_DAMAGE_DEATHS_CATEGORY_PLAYER.with(ChatColor.AQUA),
          new LocalizedNumber(deathsPlayer, numStyle)));
      if (weapon != null && weapon != Material.AIR) {
        result.add(Translations.STATS_DAMAGE_KILLS_CAUSE_WEAPON
            .with(ChatColor.AQUA, weapon.name().replace("_", " ").toLowerCase()));
      }
      if (distance != null && distance.intValue() > 10) {
        result.add(Translations.STATS_DAMAGE_DEATHS_CAUSE_BOW
            .with(ChatColor.AQUA, new LocalizedNumber(distance, 1, 1, numStyle)));
      }
    }

    return result;
  }

  public static Localizable getRandomMatchFact(List<Localizable> source) {
    return new UnlocalizedFormat("{0} {1}")
        .with(Translations.STATS_FACTS_RANDOM.with(ChatColor.GOLD),
            source.get(random.nextInt(source.size())));
  }

  public static List<Localizable> getMatchFacts(LifetimeStore store) {
    return getMatchFacts(store.getPlayerLifetimes().values().stream()
        .flatMap(lifetime -> lifetime.getActions().stream()).collect(Collectors.toList()));
  }

  public static List<Localizable> getMatchFacts(List<PlayerAction> actions) {
    List<Localizable> result = new ArrayList<>();

    UnlocalizedFormat combined = new UnlocalizedFormat("{0} {1}");

    int kills = (int) actions.stream()
        .filter(playerAction -> playerAction instanceof PlayerKillAction).count();
    if (kills > 4) {
      // Killer
      Multiset.Entry<Player> killer = CollectionUtils
          .mostCommonAttributeEntry(actions, PlayerKillAction.class, (playerAction -> {
            PlayerKillAction action = (PlayerKillAction) playerAction;
            return action.getActor();
          }));
      if (killer != null && killer.getElement() != null) {
        result.add(combined.with(Translations.STATS_FACTS_KILLS_MOST_TEXT
            .with(ChatColor.AQUA, new UnlocalizedText(killer.getElement().getDisplayName()),
                new LocalizedNumber(killer.getCount())), randomTag(killMostTags)));
      }

      // Weapon
      Multiset.Entry<Material> weapon = CollectionUtils
          .mostCommonAttributeEntry(actions, PlayerKillAction.class, (playerAction -> {
            PlayerKillAction action = (PlayerKillAction) playerAction;
            if (action.getInfo() instanceof MeleeDamageInfo) {
              return ((MeleeDamageInfo) action.getInfo()).getWeapon();
            }
            return null;
          }));
      if (weapon != null && weapon.getElement() != null && weapon.getElement() != Material.AIR) {
        result.add(Translations.STATS_FACTS_KILLS_WEAPON_MOST.with(ChatColor.AQUA,
            new UnlocalizedText(weapon.getElement().name().replace("_", " ").toLowerCase()),
            new LocalizedNumber(weapon.getCount())));
      }

      // Bow
      Function<PlayerAction, Number> distanceFunc = (playerAction -> {
        PlayerKillAction action = (PlayerKillAction) playerAction;
        if (action.getInfo() instanceof ProjectileDamageInfo) {
          return ((ProjectileDamageInfo) action.getInfo()).getDistance();
        }
        return 0.0;
      });
      PlayerAction longest = CollectionUtils
          .highestNumberObject(actions, PlayerKillAction.class, distanceFunc);
      if (longest != null) {
        Number distance = distanceFunc.apply(longest);
        if (distance != null && distance.intValue() > 10) {
          result.add(combined.with(Translations.STATS_FACTS_SNIPE_TEXT
              .with(ChatColor.AQUA, new UnlocalizedText(longest.getActor().getDisplayName()),
                  new LocalizedNumber(distance, 1, 1, numStyle)), randomTag(snipeTags)));
        }
      }
    }

    int killAssists = (int) actions.stream()
        .filter(playerAction -> playerAction instanceof PlayerAssistKillAction).count();
    if (killAssists > 4) {
      // Helpful
      Multiset.Entry<Player> helpful = CollectionUtils
          .mostCommonAttributeEntry(actions, PlayerAssistKillAction.class, (playerAction -> {
            PlayerAssistKillAction action = (PlayerAssistKillAction) playerAction;
            return action.getActor();
          }));
      if (helpful != null) {
        result.add(combined.with(Translations.STATS_FACTS_KILLS_ASSISTS_HIGHEST
            .with(ChatColor.AQUA, new UnlocalizedText(helpful.getElement().getDisplayName()),
                new LocalizedNumber(helpful.getCount())), randomTag(killAssistTags)));
      }
    }

    int deaths = (int) actions.stream()
        .filter(playerAction -> playerAction instanceof PlayerDeathByNaturalAction).count();
    if (deaths > 5) {
      Function<PlayerAction, Number> fallFunc = value -> {
        PlayerDeathByNaturalAction action = (PlayerDeathByNaturalAction) value;
        if (action.getInfo() instanceof FallDamageInfo) {
          return (double) ((FallDamageInfo) action.getInfo()).getFallDistance();
        }

        return 0.0;
      };
      PlayerAction highestFall = CollectionUtils
          .highestNumberObject(actions, PlayerDeathByNaturalAction.class, fallFunc);
      if (highestFall != null) {
        Number distance = fallFunc.apply(highestFall);
        if (distance.intValue() > 30) {
          result.add(combined.with(Translations.STATS_FACTS_FALL_TEXT
              .with(ChatColor.AQUA, new UnlocalizedText(highestFall.getActor().getDisplayName()),
                  new LocalizedNumber(distance, numStyle)), randomTag(fallTags)));
        }
      }
    }

    if (result.isEmpty()) {
      result.add(Translations.STATS_FACTS_ERROR_NONE.with(ChatColor.RED));
    }

    return result;
  }

  public static List<Localizable> getObjectiveDisplay(PlayerLifetime lifetime,
      CommandSender sender) {
    ArrayListMultimap<Objective, ObjectiveAction> map = ArrayListMultimap.create();
    lifetime.getActions().stream().filter(playerAction -> playerAction instanceof ObjectiveAction)
        .forEach(
            action -> map.put(((ObjectiveAction) action).getActed(), (ObjectiveAction) action));

    return getObjectiveDisplay(map, sender);
  }

  public static List<Localizable> getObjectiveDisplay(LifetimeStore store, UUID uuid,
      CommandSender sender) {
    ArrayListMultimap<Objective, ObjectiveAction> map = ArrayListMultimap.create();
    for (PlayerLifetime lifetime : store.getPlayerLifetimes().get(uuid)) {
      lifetime.getActions().stream().filter(playerAction -> playerAction instanceof ObjectiveAction)
          .forEach(action -> {
            map.put(((ObjectiveAction) action).getActed(), (ObjectiveAction) action);
          });
    }

    return getObjectiveDisplay(map, sender);
  }

  public static List<Localizable> getObjectiveDisplay(
      ArrayListMultimap<Objective, ObjectiveAction> map, CommandSender sender) {
    List<Localizable> result = new ArrayList<>();

    map.keySet().forEach(objective -> {
      String name = objective.getName(sender);
      HashMap<Class, List<ObjectiveAction>> actionsByType = new HashMap<>();
      for (ObjectiveAction action : map.get(objective)) {
        actionsByType.putIfAbsent(action.getClass(), new ArrayList<>());
        actionsByType.get(action.getClass()).add(action);
      }
      actionsByType.values().forEach(actions -> {
        boolean plural = actions.size() > 1;
        LocalizedFormat message = actions.get(0).actionMessage(plural);
        if (plural) {
          result.add(message.with(ChatColor.GREEN, new UnlocalizedText(name),
              new LocalizedNumber(actions.size(), numStyle)));
        } else {
          result.add(message.with(ChatColor.AQUA, new UnlocalizedText(name)));
        }
      });
    });

    return result;
  }
}
