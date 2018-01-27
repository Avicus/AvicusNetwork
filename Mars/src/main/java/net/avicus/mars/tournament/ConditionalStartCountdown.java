package net.avicus.mars.tournament;

import net.avicus.atlas.countdown.StartingCountdown;
import net.avicus.atlas.match.Match;
import net.avicus.compendium.locale.text.UnlocalizedText;
import org.bukkit.Bukkit;
import org.joda.time.Duration;

/**
 * A countdown initiated when all tournament conditions are met.
 */
public class ConditionalStartCountdown extends StartingCountdown {

  private final TournamentMatch tournamentMatch;

  public ConditionalStartCountdown(Match match, TournamentMatch tournamentMatch) {
    super(match);
    this.tournamentMatch = tournamentMatch;
  }


  @Override
  public void onTick(Duration elapsedTime, Duration remainingTime) {
    if (Bukkit.getOnlinePlayers().isEmpty()) {
      resetElapsedTime();
      return;
    }

    int neededRegister = tournamentMatch.getRemainingUnregistered();

    if (neededRegister > 0) {
      super.updateBossBar(new UnlocalizedText(
              neededRegister + " teams need to be registered in order for the match to be playable."),
          elapsedTime);
      resetElapsedTime();
      return;
    }

    if (!tournamentMatch.canStart()) {
      super.updateBossBar(new UnlocalizedText(
              "All teams (and the referee team) need to be ready in order for the match to start."),
          elapsedTime);
      resetElapsedTime();
      return;
    }

    // Let StartingCountdown handle the rest.
    super.onTick(elapsedTime, remainingTime);
  }
}