package net.avicus.hook.gadgets.types.badge;

import lombok.Getter;

public enum BadgeSymbol {

  // Chars
  ASTERISK('*', 0.0),
  DOT('°', 0.5),
  COPYRIGHT('©', 1.2),
  REGISTERED('®', 1.4),
  TRADEMARK('™', 2.0),

  // Math
  HOLLOW_ISOTOXAL('✧', 2.2),
  ISOTOXAL('✦', 2.3),
  INTERSECTION('✳', 2.0),
  INTERSECTION_THICK('❋', 2.2),
  INFINITY('∞', 4.0),
  NULL_SET('∅', 3.5),
  NOT_EQUAL('≠', 4.4),
  ALMOST_EQUAL('≈', 4.2),

  // Chess
  KING('♚', 100.0),
  QUEEN('♛', 60.3),
  ROOK('♜', 45.0),
  BISHOP('♝', 35.0),
  KNIGHT('♞', 20.0),
  PAWN('♟', 10.0),

  // Aerospace
  PROPELLER('✣', 43.0),
  AIRPLANE('✈', 35.0),

  // Greek
  THETA('Θ', 15.0),
  SIGMA('Σ', 15.0),
  OMEGA('Ω', 55.0),
  DELTA('∆', 45.0),
  DIGAMMA('Ͷ', 25.0),
  XI('Ξ', 22.0),
  PHI('Φ', 15.0),
  PSI('Ψ', 32.0),

  // Cards
  SPADE('♠', 36.0),
  DIAMOND('♢', 66.0),

  // Nature
  SNOWFLAKE('❄', 50.0),
  STAR_BADGE('✪', 66.0),
  SUN_BADGE('❂', 60.0),
  STAR('✭', 70.0),
  FLOWER('❀', 55.0),

  // Misc
  SMILEY('ツ', 66.0),
  HOT_BEVERAGE('☕', 66.0),
  MUSIC_NOTES('♫', 54.0),
  HEART('❤', 20.0),
  YIN_YANG('☯', 39.0),
  BIOHAZARD('☣', 120.0),
  ARROW('➤', 73.0);

  @Getter
  private char character;
  @Getter
  private float inflation;

  BadgeSymbol(char character, double inflation) {
    this.character = character;
    this.inflation = (float) inflation;
  }

  @Override
  public String toString() {
    return this.character + "";
  }

  public int getPrice(int original) {
    return Math.round(original + (original * this.inflation));
  }
}
