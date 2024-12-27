package net.avicus.atlas.module.results.scenario;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.countdown.MatchEndCountdown;
import net.avicus.atlas.event.competitor.CompetitorPlaceEvent;
import net.avicus.atlas.event.competitor.CompetitorWinEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.modifiers.AllCheck;
import net.avicus.atlas.module.checks.modifiers.AllowCheck;
import net.avicus.atlas.module.checks.modifiers.AnyCheck;
import net.avicus.atlas.module.checks.modifiers.DenyCheck;
import net.avicus.atlas.module.checks.modifiers.NotCheck;
import net.avicus.atlas.module.checks.types.TimeCheck;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.results.RankingDisplay;
import net.avicus.atlas.module.results.ResultsModule;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.number.NumberComparator;
import net.avicus.compendium.utils.Strings;
import org.bukkit.ChatColor;

@ToString
public abstract class EndScenario {

  @Getter
  private final Check check;
  @Getter
  private final int places;
  @Getter
  private Optional<MatchEndCountdown> countdown = Optional.empty();

  public EndScenario(Match match, Check check, int places) throws RuntimeException {
    this.check = check;
    this.places = places;

    validateTimeCheck(match, check);
  }

  private void validateTimeCheck(Match match, Check check) {
    if (check instanceof AllCheck) {
      ((AllCheck) check).getChildren().forEach(c -> validateTimeCheck(match, c));
    }

    if (check instanceof AnyCheck) {
      ((AnyCheck) check).getChildren().forEach(c -> validateTimeCheck(match, c));
    }

    if (check instanceof AllowCheck) {
      validateTimeCheck(match, ((AllowCheck) check).getChild());
    }

    if (check instanceof DenyCheck) {
      validateTimeCheck(match, ((DenyCheck) check).getChild());
    }

    if (check instanceof NotCheck) {
      validateTimeCheck(match, ((NotCheck) check).getChild());
    }

    if (check instanceof TimeCheck) {
      if (!((TimeCheck) check).getComparator().equals(NumberComparator.EQUALS)) {
        throw new RuntimeException("Time checks can only use equals comparators in end scenarios.");
      } else {
        this.countdown = Optional.of(new MatchEndCountdown(match, ((TimeCheck) check).getValue()
            .minus(match.getRequiredModule(StatesModule.class).getTotalPlayingDuration()), this));
      }
    }
  }

  public boolean test(Match match) {
    CheckContext context = new CheckContext(match);
    return this.check.test(context).passes();
  }

  public abstract void execute(Match match, GroupsModule groups);

  public void handleWin(Match match, Competitor competitor) {
    CompetitorWinEvent event = new CompetitorWinEvent(match, competitor);
    Events.call(event);

    match.getRequiredModule(ResultsModule.class)
        .broadcastWinners(match.getRequiredModule(GroupsModule.class).getCompetitors(),
            Collections.singletonList(competitor));
  }

  public void handleMultiWin(Match match, RankingDisplay display) {
    for (Map.Entry<Integer, HashSet<Competitor>> entry : display.getRanking().entrySet()) {
      for (Competitor competitor : entry.getValue()) {
        CompetitorPlaceEvent event = new CompetitorPlaceEvent(match, competitor,
            display.getRanking().headMap(entry.getKey()).size());
        Events.call(event);
      }
    }

    final Localizable translation = Messages.UI_WINNERS.with();
    match.getPlayers().forEach(player -> player.sendMessage(Strings
        .padChatComponent(translation.render(player), "-", ChatColor.GOLD,
            ChatColor.YELLOW)));
    display.getRankDisplay()
        .forEach(match::broadcast);
  }

}
