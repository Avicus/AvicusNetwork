package net.avicus.atlas.module.checks.types;

import lombok.ToString;

/**
 * A sometimes check is a random check with a randomness of 50%.
 */
@ToString
public class SometimesCheck extends RandomCheck {

  public SometimesCheck() {
    super(0.5);
  }
}
