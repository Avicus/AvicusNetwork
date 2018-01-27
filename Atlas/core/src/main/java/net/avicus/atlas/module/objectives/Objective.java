package net.avicus.atlas.module.objectives;

import java.util.Optional;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface Objective {

  /**
   * Called after world generation.
   */
  void initialize();

  /**
   * @return The name of the objective
   */
  LocalizedXmlString getName();

  default String getName(CommandSender viewer) {
    return getName().translate(viewer.getLocale());
  }

  /**
   * @return If this objective can be completed by a specified competitor.
   */
  boolean canComplete(Competitor competitor);

  default boolean canComplete(Optional<Competitor> competitor) {
    if (!competitor.isPresent()) {
      return false;
    }
    return canComplete(competitor.get());
  }

  default boolean canComplete(Player player) {
    Optional<Competitor> competitor = Atlas.getMatch().getRequiredModule(GroupsModule.class)
        .getCompetitorOf(player);
    return canComplete(competitor);
  }

  /**
   * @return If the objective is complete.
   */
  boolean isCompleted(Competitor competitor);

  /**
   * Get the completion of the monument.
   *
   * @return The completion value.
   */
  double getCompletion(Competitor competitor);

  /**
   * Determines if this objective is incremental.
   */
  boolean isIncremental();

  /**
   * If the objective should be displayed.
   *
   * @return {@code true} if the objective should be displayed, {@code false} otherwise
   */
  default boolean show() {
    return true;
  }
}
