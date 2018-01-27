package net.avicus.hook.gadgets.types.trail;

import lombok.Getter;
import net.avicus.hook.utils.ParticleEffect;

@Getter
public enum TrailType {
  // Todo: Translated names

  PORTAL("Portal", ParticleEffect.PORTAL),
  RUNES("Runes", ParticleEffect.ENCHANTMENT_TABLE),
  CRITICAL("Critical", ParticleEffect.CRIT),
  CRITICAL_MAGIC("Critical Magic", ParticleEffect.CRIT_MAGIC),
  CLOUD("Cloud", ParticleEffect.EXPLOSION_NORMAL),
  VOID("Void", ParticleEffect.TOWN_AURA),
  NOTE("Note", ParticleEffect.NOTE),
  SPELL("Spell", ParticleEffect.SPELL),
  WITCH("Witch", ParticleEffect.SPELL_WITCH),
  SLIME("Slime", ParticleEffect.SLIME),
  FIRE("Fire", ParticleEffect.FLAME),
  EMBER("Ember", ParticleEffect.LAVA),
  HEART("Heart", ParticleEffect.HEART),
  RAINBOW("Rainbow", ParticleEffect.SPELL_MOB);

  private final String name;
  private final ParticleEffect effect;

  TrailType(String name, ParticleEffect effect) {
    this.name = name + " Trail";
    this.effect = effect;
  }
}
