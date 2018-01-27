package net.avicus.atlas.module.kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.event.player.PlayerSpawnCompleteEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.kits.menu.KitMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@ToString(exclude = "match")
public class KitsModule implements Module {

  private final Match match;
  private final Optional<Kit> defaultKit;
  @Getter
  private final List<Kit> kits;
  private final Map<Player, Kit> currentKits;
  private final Map<Player, Kit> upcomingKits;

  public KitsModule(Match match, Optional<Kit> defaultKit, List<Kit> kits) {
    this.match = match;
    this.defaultKit = defaultKit;
    this.kits = kits;

    this.currentKits = new HashMap<>();
    this.upcomingKits = new HashMap<>();
  }

  @Override
  public void open() {
    this.kits.forEach(Kit::enable);
  }

  @Override
  public void close() {
    this.kits.forEach(Kit::disable);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerSpawn(PlayerSpawnCompleteEvent event) {
    final Player player = event.getPlayer();
    if (event.getGroup().isObserving()) {
      if (event.isGiveLoadout() && isEnabled(event.getPlayer())) {
        player.getInventory().addItem(KitMenu.create(event.getPlayer()));
      }

      return;
    }

    // Update current kit
    updateCurrentKit(player);

    if (event.isGiveLoadout()) {
      Kit kit = getCurrentKit(player).orElse(null);

      if (kit == null) {
        return;
      }

      kit.apply(player);
    }
  }

  public boolean isEnabled(Player player) {
    return !getKits(player).isEmpty();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerQuit(PlayerQuitEvent event) {
    this.currentKits.remove(event.getPlayer());
    this.upcomingKits.remove(event.getPlayer());
  }

  public List<Kit> getKits(Player player) {
    CheckContext context = new CheckContext(this.match);
    context.add(new PlayerVariable(player));
    return this.kits.stream().filter(
        k -> !k.getApplicationCheck().isPresent() || k.getApplicationCheck().get().test(context)
            .passes()).collect(Collectors.toList());
  }

  /**
   * Update a player's current kit with what they have selected prior to
   * calling this method.
   */
  private void updateCurrentKit(Player player) {
    Kit upcoming = this.upcomingKits.get(player);

    CheckContext context = new CheckContext(this.match);
    context.add(new PlayerVariable(player));

    Kit current = getCurrentKit(player).orElse(null);

    if (current != null && current.getApplicationCheck().isPresent() && current
        .getApplicationCheck().get().test(context).fails()) {
      upcoming = defaultKit.orElse(null);
      if (upcoming == null) {
        this.currentKits.remove(player);
      }
    }

    if (upcoming == null) {
      return;
    }

    if (upcoming.getApplicationCheck().isPresent() && upcoming.getApplicationCheck().get()
        .test(context).fails()) {
      return;
    }

    this.currentKits.put(player, upcoming);
    this.upcomingKits.remove(player);

    if (current != null) {
      current.removePermissions(player);
    }
  }

  /**
   * Get the kit that a player currently has.
   */
  public Optional<Kit> getCurrentKit(Player player) {
    if (this.currentKits.containsKey(player)) {
      return Optional.of(this.currentKits.get(player));
    }
    return this.defaultKit;
  }

  /**
   * Get the kit that a player currentl has, and is active (in-game, during match).
   */
  public Optional<Kit> getActiveKit(Player player) {
    Optional<Kit> kit = getCurrentKit(player);
    if (!kit.isPresent()) {
      return Optional.empty();
    }

    if (this.match.getRequiredModule(GroupsModule.class).isObservingOrDead(player)) {
      return Optional.empty();
    }

    return kit;
  }

  /**
   * Set a player's kit. Does not apply, takes effect on spawn or when applied manually.
   */
  public void setUpcomingKit(Player player, Kit kit) {
    this.upcomingKits.put(player, kit);
  }

  /**
   * Search for a kit.
   */
  public List<Kit> search(CommandSender viewer, String query) {
    List<Kit> result = new ArrayList<>();
    Locale locale = viewer.getLocale();

    List<Kit> kits = this.kits;
    if (viewer instanceof Player) {
      kits = this.getKits((Player) viewer);
    }

    for (Kit kit : kits) {
      String translated = kit.getName().toText().translate(locale).toPlainText();
      if (translated.toLowerCase().startsWith(query.toLowerCase())) {
        result.add(kit);
      }
    }

    result.sort((o1, o2) -> {
      String name1 = o1.getName().translate(viewer).toLowerCase();
      String name2 = o2.getName().translate(viewer).toLowerCase();

      if (name1.equals(query.toLowerCase())) {
        return 1;
      }

      if (name2.equals(query.toLowerCase())) {
        return -1;
      }

      if (name1.startsWith(query.toLowerCase()) && !name2.startsWith(query.toLowerCase())) {
        return 1;
      }

      if (!name1.startsWith(query.toLowerCase()) && name2.startsWith(query.toLowerCase())) {
        return -1;
      }

      return 0;
    });

    return result;
  }

  @EventHandler
  public void playerInteract(final PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_AIR
        && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (KitMenu.matches(event.getItem())) {
      KitMenu.create(this, event.getPlayer()).open();
    }
  }
}
