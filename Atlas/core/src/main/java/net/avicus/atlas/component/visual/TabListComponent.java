package net.avicus.atlas.component.visual;

import com.google.common.collect.Lists;
import com.keenant.tabbed.Tabbed;
import com.keenant.tabbed.item.BlankTabItem;
import com.keenant.tabbed.item.PlayerTabItem;
import com.keenant.tabbed.item.TabItem;
import com.keenant.tabbed.item.TextTabItem;
import com.keenant.tabbed.tablist.TabList;
import com.keenant.tabbed.tablist.TableTabList;
import com.keenant.tabbed.tablist.TableTabList.FillDirection;
import com.keenant.tabbed.tablist.TableTabList.TableBox;
import com.keenant.tabbed.tablist.TableTabList.TableCell;
import com.keenant.tabbed.tablist.TableTabList.TableCorner;
import com.keenant.tabbed.util.Skins;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import com.viaversion.viaversion.api.Via;
import lombok.Getter;
import lombok.Setter;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.event.RefreshUIEvent;
import net.avicus.atlas.event.group.GroupMaxPlayerCountChangeEvent;
import net.avicus.atlas.event.group.GroupRenameEvent;
import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import net.avicus.atlas.event.match.MatchOpenEvent;
import net.avicus.atlas.event.match.MatchStateChangeEvent;
import net.avicus.atlas.event.match.MatchTickEvent;
import net.avicus.atlas.event.player.PlayerJoinDelayedEvent;
import net.avicus.atlas.map.AtlasMap;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.display.PlayerSidebarProvider;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.StringUtil;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.module.ListenerModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

public class TabListComponent implements ListenerModule {

  public static final int ROWS = 20;
  public static final int COLUMNS = 4;

  @Getter
  @Setter
  private Match match;
  @Getter
  private Tabbed tabbed;
  private Map<Group, TableBox> teamBoxes;
  private List<TabItem> blanks;

  @Override
  public void enable() {
    this.tabbed = new Tabbed(Atlas.get());
    this.blanks = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      this.blanks.add(new BlankTabItem());
    }
  }

  public void update(boolean purgeData) {
    Bukkit.getOnlinePlayers()
        .forEach((player) -> {
          AtlasTask.of(() -> this.update(player, purgeData)).nowAsync();
        });
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onMatchOpen(MatchOpenEvent event) {
    setMatch(event.getMatch());
    this.teamBoxes = createTeamBoxes();
    update(true);
  }

  @EventHandler
  public void onMatchTick(MatchTickEvent event) {
    Bukkit.getOnlinePlayers()
        .forEach(this::updateTitles);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerJoin(PlayerJoinDelayedEvent event) {
    // delayed by 1 1/2 second for locale to be sent
    new AtlasTask() {
      @Override
      public void run() {
        int version = Via.getAPI().getPlayerVersion(event.getPlayer());
        if (event.getPlayer().isOnline() && version >= 47) {
          TabListComponent.this.tabbed.newTableTabList(event.getPlayer(), COLUMNS, 16);
          update(event.getPlayer(), false);
        }
      }
    }.laterAsync(30);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    update(false);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onChangeTeam(PlayerChangedGroupEvent event) {
    update(false);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onStateChangeEvent(MatchStateChangeEvent event) {
    if (event.getTo().isPresent() && !event.getTo().get().getId().equals("starting")) {
      update(true);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onGroupRename(GroupRenameEvent event) {
    update(true);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void groupMaxPlayersChange(final GroupMaxPlayerCountChangeEvent event) {
    this.update(true);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onRefresh(final RefreshUIEvent event) {
    this.update(true);
  }

  private Map<Group, TableBox> createTeamBoxes() {
    GroupsModule module = this.match.getRequiredModule(GroupsModule.class);

    Map<Group, TableBox> teamBoxes = new HashMap<>();

    int i = 0;
    for (Group team : module.getGroups()) {
      if (team.isSpectator()) {
        continue;
      }
      TableCell left = new TableCell(i, 1);
      TableCell right = new TableCell(i, ROWS - 4);
      teamBoxes.put(team, new TableBox(left, right));
      i++;
    }

    if (teamBoxes.size() == 1) {
      TableBox box = teamBoxes.values().iterator().next();
      box.getTopRight().setColumn(COLUMNS - 1);
      box.getBottomRight().setColumn(COLUMNS - 1);
    } else if (teamBoxes.size() == 2) {
      i = 0;
      for (TableBox box : teamBoxes.values()) {
        box.getTopLeft().setColumn(i * 2);
        box.getBottomLeft().setColumn(i * 2);
        box.getTopRight().setColumn(i * 2 + 1);
        box.getBottomRight().setColumn(i * 2 + 1);
        i++;
      }
    } else if (teamBoxes.size() > 4) {
      int size = teamBoxes.size();
      List<TableBox> boxes = new ArrayList<>(teamBoxes.values());
      List<List<TableBox>> partitioned = Lists.partition(boxes, size / 4);
      for (List<TableBox> partition : partitioned) {
        int column = partitioned.indexOf(partition);
        int height = ((ROWS - 4) / (partition.size()));
        for (TableBox box : partition) {
          int initialRow = partition.indexOf(box) + 1;
          if (initialRow > 1) {
            initialRow = initialRow + height;
          }

          box.getTopLeft().setRow(initialRow);
          box.getTopLeft().setColumn(column);

          box.getBottomLeft().setRow(initialRow + height);
          box.getBottomLeft().setColumn(column);

          box.getTopRight().setRow(initialRow);
          box.getTopRight().setColumn(column);

          box.getBottomRight().setRow(initialRow + height);
          box.getBottomRight().setColumn(column);
        }
      }
    }

    return teamBoxes;
  }

  private TableTabList getTabList(Player player) {
    TabList tab = this.tabbed.getTabList(player);
    return (TableTabList) tab;
  }

  public void updateTitles(Player player) {
    @Nullable final TableTabList tab = this.getTabList(player);
    if (tab == null) {
      return;
    }

    final AtlasMap map = this.match.getMap();
    final UnlocalizedText mapPart = new UnlocalizedText(map.getName());
    mapPart.style().color(ChatColor.AQUA).bold(true);
    final UnlocalizedText authorPart = new UnlocalizedText(StringUtil
        .join(map.getAuthors(), ChatColor.GRAY + ", ",
            author -> ChatColor.DARK_AQUA + author.getName()));
    authorPart.style().color(ChatColor.GRAY).bold(true);
    final StatesModule sm = this.match.getRequiredModule(StatesModule.class);
    final ChatColor stateColor =
        sm.isStarting() ? ChatColor.WHITE : sm.isPlaying() ? ChatColor.GREEN : ChatColor.RED;
    final String time = StringUtil
        .secondsToClock((int) sm.getTotalPlayingDuration().getStandardSeconds());
    final String header = Messages.UI_BY.with(ChatColor.GRAY, mapPart, authorPart)
        .render(player).toLegacyText();
    String footer = stateColor + "" + ChatColor.BOLD + time;

    @Nullable String serverName = Atlas.get().getBridge().getServerName();
    if (serverName != null) {
      footer = ChatColor.GOLD + "" + ChatColor.BOLD + serverName + ChatColor.GRAY + " - " + footer;
    }

    tab.setHeaderFooter(header, footer);
  }

  public void update(Player player, boolean purgeData) {
    TableTabList tab = getTabList(player);

    if (tab == null) {
      return;
    }

    tab.setBatchEnabled(true);

    if (purgeData) {
      List<TabItem> blanks = new ArrayList<>();
      for (int i = 0; i < ROWS * COLUMNS; i++) {
        blanks.add(new BlankTabItem());
      }
      tab.fill(tab.getBox(), blanks);
    }

    if (!player.isOnline()) {
      return;
    }

    updateTitles(player);

    // Spectators
    {
      boolean spec = this.match.getRequiredModule(GroupsModule.class).isSpectator(player);

      List<TabItem> specs = new ArrayList<>();
      if (spec) {
        specs.add(getTabItem(player, player));
      }
      for (Player target : Bukkit.getOnlinePlayers()) {
        if (target.equals(player) || !target.isOnline()) {
          continue;
        }

        try {
          if (this.match.getRequiredModule(GroupsModule.class).isSpectator(target)) {
            specs.add(getTabItem(target, player));
          }
        } catch (RuntimeException e) {
          // Ignored - no in a group
        }
      }
      specs.addAll(this.blanks);

      TableBox spectatorBox = new TableBox(tab.getBox().getBottomLeft().clone().add(0, -2),
          tab.getBox().getBottomRight().clone());
      tab.fill(spectatorBox, specs, TableCorner.BOTTOM_LEFT);
    }

    // Teams
    {
      for (Map.Entry<Group, TableBox> entry : this.teamBoxes.entrySet()) {
        Group team = entry.getKey();
        TableBox box = entry.getValue();

        List<TabItem> items = new ArrayList<>();
        if (team.isMember(player)) {
          items.add(getTabItem(player, player));
        }
        for (Player target : team.getPlayers()) {
          if (target.equals(player) || !target.isOnline()) {
            continue;
          }
          items.add(getTabItem(target, player));
        }
        items.addAll(this.blanks);

        TabItem teamItem = getTabItem(team, player);

        tab.set(box.getTopLeft().clone().add(0, -1), teamItem);
        tab.fill(box, items, TableCorner.TOP_LEFT, FillDirection.VERTICAL);
      }
    }

    tab.batchUpdate();
    try {
      tab.setBatchEnabled(false);
    } catch (RuntimeException e) {
      // We do this because tabbed really isn't thread safe, but we handle it internally.
      if (!e.getMessage().equals("cannot disable batch before batchUpdate() called")) {
        e.printStackTrace();
      }
    }
  }

  private TextTabItem getTabItem(Group group, Player viewer) {
    String name = group.getName().toText(group.getChatColor()).render(viewer)
        .toLegacyText();
    String text =
        ChatColor.WHITE.toString() + group.size() + ChatColor.GRAY.toString() + "/" + group
            .getMaxPlayers() + " " + name;
    return new TextTabItem(text, 1000, Skins.getDot(group.getChatColor()));
  }

  private PlayerTabItem getTabItem(Player player, Player viewer) {
    return new PlayerTabItem(player, getProvider(viewer));
  }

  private PlayerSidebarProvider getProvider(Player viewer) {
    return new PlayerSidebarProvider(this.match, viewer);
  }
}
