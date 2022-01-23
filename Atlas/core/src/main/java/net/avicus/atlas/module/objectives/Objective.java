package net.avicus.atlas.module.objectives;

import java.util.Optional;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.event.RefreshUIEvent;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.runtimeconfig.RuntimeConfigurable;
import net.avicus.atlas.runtimeconfig.fields.ConfigurableField;
import net.avicus.atlas.runtimeconfig.fields.LocalizedXmlField;
import net.avicus.atlas.util.Events;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface Objective extends RuntimeConfigurable {

  /**
   * Called after world generation.
   */
  void initialize();

  /**
   * @return The name of the objective
   */
  LocalizedXmlString getName();

  /**
   * Set a new name for the objective
   * @param name to set
   */
  void setName(LocalizedXmlString name);

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

  @Override
  default String getDescription(CommandSender viewer) {
    return getClass().getSimpleName() + " (" + getName(viewer) + ")";
  }

  @Override
  default void onFieldChange(String name) {
    Events.call(new RefreshUIEvent());
  }

  @Override
  default ConfigurableField[] getFields() {
    return new ConfigurableField[]{
        new LocalizedXmlField("Name", this::getName, this::setName)
    };
  }
}
