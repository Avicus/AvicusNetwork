package net.avicus.atlas.module.elimination;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.event.group.PlayerChangeGroupEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.elimination.event.PlayerEliminateEvent;
import net.avicus.atlas.module.elimination.event.TeamEliminateEvent;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.Spectators;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.grave.event.PlayerDeathEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.scheduler.BukkitRunnable;

@ToString(exclude = "match")
public class EliminationModule implements Module {

  private final Match match;
  private final int maxLives;
  private final boolean strikeOnEliminate;
  private final Optional<Check> check;
  private final Map<UUID, Integer> lives;
  private final Spectators spectators;
  @Getter
  @Setter
  private boolean enabled;

  public EliminationModule(Match match, int maxLives, boolean enabled, boolean strikeOnEliminate,
      Optional<Check> check) {
    this.match = match;
    this.maxLives = maxLives;
    this.enabled = enabled;
    this.strikeOnEliminate = strikeOnEliminate;
    this.check = check;
    this.lives = new HashMap<>();
    this.spectators = match.getRequiredModule(GroupsModule.class).getSpectators();
  }

  public int getLives(Player player) {
    return this.lives.getOrDefault(player.getUniqueId(), this.maxLives);
  }

  private int removeLife(Player player) {
    int next = getLives(player) - 1;
    this.lives.put(player.getUniqueId(), next);
    return next;
  }

  public void setLives(Player player, int lives) {
    this.lives.put(player.getUniqueId(), lives);
  }

  @EventHandler
  public void onPlayerChangeGroup(PlayerChangeGroupEvent event) {
    if (!this.enabled) {
      return;
    }

    if (event.isForce()) {
      return;
    }

    if (event.getGroup() instanceof Spectators) {
      return;
    }
    if (this.match.getRequiredModule(StatesModule.class).isStarting()) {
      return;
    }

    event.getPlayer().sendMessage(Messages.ERROR_CANNOT_JOIN_ONGOING.with(ChatColor.RED));
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerDeath(PlayerDeathEvent event) {
    if (!this.enabled) {
      return;
    }

    if (this.check.isPresent()) {
      Check check = this.check.get();
      CheckContext context = new CheckContext(this.match);
      context.add(new PlayerVariable(event.getPlayer()));
      if (check.test(context).fails()) {
        return;
      }
    }

    int lives = removeLife(event.getPlayer());

    if (lives == 0) {
      Localizable noLives = Messages.UI_IMPORTANT
          .with(TextStyle.ofBold(), Messages.GENERIC_NO_LIVES.with());
      event.getPlayer().sendMessage(noLives);

      eliminate(event.getPlayer());
    } else {
      LocalizedNumber number = new LocalizedNumber(lives,
          TextStyle.ofColor(ChatColor.DARK_RED).bold());
      if (lives == 1) {
        event.getPlayer().sendMessage(Messages.GENERIC_LIVES.with(number));
      } else {
        event.getPlayer().sendMessage(Messages.GENERIC_LIVES_PLURAL.with(number));
      }
    }
  }

  public void eliminate(Player player) {
    if (this.strikeOnEliminate) {
      this.match.getWorld().strikeLightningEffect(player.getLocation());
    }

    PlayerEliminateEvent call = new PlayerEliminateEvent(player);
    Events.call(call);

    Group group = this.match.getRequiredModule(GroupsModule.class).getGroup(player);

    new BukkitRunnable() {
      @Override
      public void run() {
        EliminationModule.this.match.getRequiredModule(GroupsModule.class)
            .changeGroup(player, EliminationModule.this.spectators, true, false);

        if (group.size() == 0 && group instanceof Team) {
          TeamEliminateEvent teamEliminateEvent = new TeamEliminateEvent((Team) group);
          Events.call(teamEliminateEvent);
        }
      }
    }.runTaskLater(Atlas.get(), 20 * 2);
  }
}
