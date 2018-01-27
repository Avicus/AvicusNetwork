package net.avicus.atlas.sets.arcade;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Setter;
import net.avicus.atlas.component.visual.SidebarHook;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.magma.util.Sidebar;
import org.bukkit.entity.Player;

public class SBHook extends SidebarHook {

  private final ArcadeMain arcadeMain;

  @Setter
  private ArcadeGame currentGame;

  public SBHook(ArcadeMain arcadeMain) {
    this.arcadeMain = arcadeMain;
  }

  @Override
  public List<String> getRows(Player player, GroupsModule groups, Sidebar sidebar,
      ObjectivesModule module) {

    if (this.currentGame != null) {
      return currentGame.getRows(player, groups, sidebar, module);
    }

    return super.getRows(player, groups, sidebar, module);
  }

  @Override
  public Optional<Localizable> getTitle(ObjectivesModule module) {
    return Optional.of(Messages.UI_ARCADE.with());
  }

  public List<String> showIndividualDisplay(GroupsModule module) {
    List<String> lines = new ArrayList<>();

    getMatch().getPlayers().forEach(p -> {
      module.getCompetitorOf(p).ifPresent(c ->
          lines.add(p.getDisplayName())
      );
    });

    return lines;
  }
}
