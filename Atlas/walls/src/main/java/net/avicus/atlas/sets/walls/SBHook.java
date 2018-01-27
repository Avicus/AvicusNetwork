package net.avicus.atlas.sets.walls;

import static net.avicus.atlas.util.ObjectiveUtils.CHECK_MARK;
import static net.avicus.atlas.util.ObjectiveUtils.X_MARK;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.avicus.atlas.component.visual.SidebarHook;
import net.avicus.atlas.module.groups.CompetitorRule;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.magma.util.Sidebar;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SBHook extends SidebarHook {

  @Override
  public List<String> getRows(Player player, GroupsModule groups, Sidebar sidebar,
      ObjectivesModule module) {
    Optional<WallsModule> walls = this.getMatch().getModule(WallsModule.class);
    if (walls.isPresent()) {
      if (groups.getCompetitorRule() == CompetitorRule.TEAM) {
        return showTeamDisplay(player, groups, walls.get());
      } else {
        return showIndividualDisplay(player, groups, walls.get());
      }
    } else {
      return Lists.newArrayList();
    }
  }

  @Override
  public Optional<Localizable> getTitle(ObjectivesModule module) {
    return this.getMatch().hasModule(WallsModule.class) ? Optional.of(Messages.UI_WALLS.with())
        : Optional.empty();
  }

  private List<String> showTeamDisplay(Player viewer, GroupsModule module, WallsModule walls) {
    List<String> lines = new ArrayList<>();

    module.getCompetitors().forEach(g -> {
      StringBuilder res = new StringBuilder();
      res.append(g.getGroup().getMembers().isEmpty() ? ChatColor.RED + X_MARK
          : ChatColor.GREEN + CHECK_MARK);
      Localizable name = g.getColoredName();
      if (g.hasPlayer(viewer)) {
        name.style().bold();
      }
      res.append(" " + name.translate(viewer).toLegacyText());
      res.append(" " + ChatColor.GRAY + g.getGroup().getMembers().size());
      res.append(ChatColor.GOLD + "/");
      res.append(ChatColor.GRAY + "" + g.getGroup().getMaxPlayers());
      lines.add(res.toString());
    });

    return lines;
  }

  private List<String> showIndividualDisplay(Player viewer, GroupsModule module,
      WallsModule walls) {
    List<String> lines = new ArrayList<>();

    getMatch().getPlayers().forEach(p -> {
      module.getCompetitorOf(p).ifPresent(c ->
          lines.add(p.getDisplayName())
      );
    });

    return lines;
  }
}
