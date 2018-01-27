package net.avicus.atlas.module.kits.abilities;

import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.kits.KitAbility;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import tc.oc.tracker.event.PlayerDamageEvent;

@ToString
public class HealthEffectAbility extends KitAbility {

  private final PotionEffect effect;
  private final double health;

  public HealthEffectAbility(Match match, PotionEffect effect, double health) {
    super(match);
    this.effect = effect;
    this.health = health;
  }

  @EventHandler
  public void onDamage(PlayerDamageEvent event) {
    if (!hasAbility(event.getEntity(), true)) {
      return;
    }

    if (event.getEntity().getHealth() <= this.health) {
      event.getEntity().addPotionEffect(this.effect);
    }
  }
}
