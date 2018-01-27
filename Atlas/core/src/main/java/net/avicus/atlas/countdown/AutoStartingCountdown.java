package net.avicus.atlas.countdown;

import java.util.ArrayList;
import java.util.List;
import net.avicus.atlas.AtlasConfig;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.joda.time.Duration;

/**
 * A countdown initiated automatically (by Atlas itself, not a player).
 */
public class AutoStartingCountdown extends StartingCountdown {

  /**
   * Time (in mills) that the last chat broadcast occurred.
   */
  private long lastAnnounce = 0;

  /**
   * Constructor.
   *
   * @param match match the countdown is being ran inside of
   */
  public AutoStartingCountdown(Match match) {
    super(match);
  }

  @Override
  public Localizable getName() {
    return Messages.GENERIC_COUNTDOWN_AUTO_STARTING_NAME.with();
  }

  @Override
  public void onStart() {
    this.lastAnnounce = System.currentTimeMillis();
  }

  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    GroupsModule module = this.match.getRequiredModule(GroupsModule.class);

    if (Bukkit.getOnlinePlayers().isEmpty()) {
      this.resetElapsedTime();
      return;
    }

    List<Group> needMorePlayers = new ArrayList<>();
    List<Group> needBalancing = new ArrayList<>();

    // Only start with the required players
    for (Group group : module.getGroups()) {
      if (group.isSpectator()) {
        continue;
      }

      int count = group.getMembers().size();
      int needed = group.getMinPlayers();

      if (count < needed) {
        needMorePlayers.add(group);
      } else if (!module.isGroupBalanced(group, 0)) {
        needBalancing.add(group);
      }
    }

    boolean cancel =
        (!needMorePlayers.isEmpty() || !needBalancing.isEmpty()) && !AtlasConfig.isScrimmage();

    if (cancel) {
      if (!needMorePlayers.isEmpty()) {
        this.updateBossBar(Messages.GENERIC_WAITING_FOR_MORE.with(), (float) 1);
      } else {
        this.updateBossBar(Messages.GENERIC_BALANCE_NEEDED.with(), (float) 1);
      }

      long now = System.currentTimeMillis();
      if (now - this.lastAnnounce > 45000) {
        this.lastAnnounce = now;

        if (!needMorePlayers.isEmpty()) {
          String textFormat = "";
          for (int i = 0; i < needMorePlayers.size(); i++) {
            textFormat += "{" + i + "}, ";
          }
          textFormat = textFormat.substring(0, textFormat.length() - 2);
          LocalizableFormat format = new UnlocalizedFormat(textFormat);

          Localizable[] args = new Localizable[needMorePlayers.size()];
          for (int i = 0; i < needMorePlayers.size(); i++) {
            LocalizableFormat groupFormat = new UnlocalizedFormat("{0} {1}");
            Group group = needMorePlayers.get(i);
            int countNeeded = group.getMinPlayers() - group.size();
            Localizable text = groupFormat
                .with(group.getChatColor(), new LocalizedNumber(countNeeded),
                    group.getName().toText());
            text.style().click(new ClickEvent(Action.RUN_COMMAND, "/join " + group.getId()));
            args[i] = text;
          }

          // Broadcast
          this.match.broadcast(Messages.GENERIC_MORE_PLAYERS_NEEDED.with(format.with(args)));
        } else {
          // Broadcast
          this.match.broadcast(Messages.GENERIC_BALANCE_NEEDED.with(ChatColor.WHITE));
        }
      }

      this.resetElapsedTime();
      return;
    }

    // Let StartingCountdown handle the rest.
    super.onTick(elapsedTime, remainingTime);
  }
}