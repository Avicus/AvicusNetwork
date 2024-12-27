package net.avicus.atlas.module.results.scenario;

import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.sound.SoundEvent;
import net.avicus.compendium.sound.SoundLocation;
import net.avicus.compendium.sound.SoundType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

/**
 * Internal scenario to handle when check passes but no winner can be clearly decided.
 */
@ToString
public class TieScenario extends EndScenario {

  public TieScenario(Match match, Check check, int places) {
    super(match, check, places);
  }

  @Override
  public void execute(Match match, GroupsModule groups) {
    match.getRequiredModule(StatesModule.class).next();

    Localizable tie = Messages.UI_TIE.with(ChatColor.GRAY);

    for (Player player : Bukkit.getOnlinePlayers()) {
      Title title = Title.builder().title(tie.render(player))
          .fadeIn(10)
          .stay(60)
          .fadeOut(20)
          .build();
      player.sendTitle(title);

      SoundEvent call = Events
          .call(new SoundEvent(player, SoundType.HMMM, SoundLocation.MATCH_TIE));
      call.getSound().play(player, 1F);
    }
    match.importantBroadcast(tie);
  }

  public void execute(Match match) {
    this.execute(match, null);
  }
}
