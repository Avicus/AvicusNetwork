package net.avicus.atlas.module.objectives;

import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.locales.LocalizedXmlString;

public abstract class AbstractObjective implements Objective {

  /**
   * The match this objective is in.
   */
  protected final Match match;
  /**
   * The name of this objective.
   */
  protected final LocalizedXmlString name;
  /**
   * If the objective should be displayed.
   */
  protected final boolean show;

  /**
   * @param match the match this objective is in
   * @param name the name of this objective
   */
  protected AbstractObjective(final Match match, final LocalizedXmlString name) {
    this(match, name, true);
  }

  /**
   * @param match the match this objective is in
   * @param name the name of this objective
   * @param show if the objective should be displayed
   */
  protected AbstractObjective(final Match match, final LocalizedXmlString name,
      final boolean show) {
    this.match = match;
    this.name = name;
    this.show = show;
  }

  @Override
  public LocalizedXmlString getName() {
    return this.name;
  }

  @Override
  public boolean show() {
    return this.show;
  }
}
