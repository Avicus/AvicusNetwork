package net.avicus.atlas.module.modifydamage;

import java.util.List;
import lombok.Data;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.AttackerVariable;
import net.avicus.atlas.module.checks.variable.EntityVariable;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.checks.variable.MaterialVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.checks.variable.VictimVariable;
import net.avicus.compendium.number.PreparedNumberAction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

@ToString(exclude = "match")
public class ModifyDamageModule implements Module {

  private final Match match;
  private final List<DamageModifier> damageMods;

  public ModifyDamageModule(Match match, List<DamageModifier> damageMods) {
    this.match = match;
    this.damageMods = damageMods;
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onDamage(EntityDamageEvent event) {
    CheckContext context = new CheckContext(this.match);
    VictimVariable victim = new VictimVariable(this.match);
    victim.add(new EntityVariable(event.getEntity()));
    if (event.getEntity() instanceof Player) {
      victim.add(new PlayerVariable((Player) event.getEntity()));
    }

    context.add(victim);

    if (event instanceof EntityDamageByEntityEvent) {
      EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
      AttackerVariable attacker = new AttackerVariable(this.match);
      attacker.add(new EntityVariable(damageEvent.getDamager()));
      context.add(attacker);
    }

    if (event instanceof EntityDamageByBlockEvent) {
      EntityDamageByBlockEvent damageByBlockEvent = (EntityDamageByBlockEvent) event;
      if (((EntityDamageByBlockEvent) event).getDamager() != null
          && ((EntityDamageByBlockEvent) event).getDamager().getState() != null &&
          ((EntityDamageByBlockEvent) event).getDamager().getType() != Material.AIR) {
        context.add(new MaterialVariable(damageByBlockEvent.getDamager().getState().getData()));
      }
    }

    context.add(new LocationVariable(event.getEntity().getLocation()));

    for (DamageModifier modifier : this.damageMods) {
      if (modifier.getCause() != event.getCause()) {
        continue;
      }

      if (modifier.getCheck().test(context).passes()) {
        double damage = modifier.getAction().perform(event.getDamage());
        if (damage <= 0) {
          event.setCancelled(true);
        } else {
          event.setDamage(damage);
        }
        break;
      }
    }
  }

  @Data
  protected static class DamageModifier {

    private final EntityDamageEvent.DamageCause cause;
    private final PreparedNumberAction action;
    private final Check check;
  }
}
