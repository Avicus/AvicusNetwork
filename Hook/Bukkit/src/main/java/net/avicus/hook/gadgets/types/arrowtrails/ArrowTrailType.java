package net.avicus.hook.gadgets.types.arrowtrails;

import java.util.List;
import lombok.Getter;
import net.avicus.hook.utils.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
public enum ArrowTrailType {

  PORTAL("Portal", ParticleEffect.PORTAL),
  RUNES("Runes", ParticleEffect.ENCHANTMENT_TABLE),
  CRITICAL_MAGIC("Critical Magic", ParticleEffect.CRIT_MAGIC),
  CLOUD("Cloud", ParticleEffect.EXPLOSION_NORMAL),
  VOID("Void", ParticleEffect.TOWN_AURA),
  NOTE("Note", ParticleEffect.NOTE),
  SPELL("Spell", ParticleEffect.SPELL),
  WITCH("Witch", ParticleEffect.SPELL_WITCH),
  SLIME("Slime", ParticleEffect.SLIME),
  EMBER("Ember", ParticleEffect.LAVA),
  RAINBOW("Rainbow", ParticleEffect.SPELL_MOB);

  private final String name;
  private final ParticleEffect effect;

  ArrowTrailType(String name, ParticleEffect effect) {
    this.name = name + " Arrow Trail";
    this.effect = effect;
  }

  public void play(Location location, List<Player> viewers) {
    getEffect().display(0.3f, 0.3f, 0.3f, 2, 5, location, viewers);
  }
}
