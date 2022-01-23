package net.avicus.atlas.module.objectives.lcs;

import java.util.Collection;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.objectives.Objective;

@ToString(exclude = "match")
public class LastCompetitorStanding implements Objective {

  private final Match match;

  public LastCompetitorStanding(Match match) {
    this.match = match;
  }

  public Collection<? extends Competitor> currentCompetitors() {
    return this.match.getRequiredModule(GroupsModule.class).getCompetitors();
  }

  @Override
  public void initialize() {

  }

  @Override
  public LocalizedXmlString getName() {
    return new LocalizedXmlString("Last Competitor Standing");
  }

  @Override
  public void setName(LocalizedXmlString name) {

  }

  @Override
  public boolean canComplete(Competitor competitor) {
    return true;
  }

  @Override
  public boolean isCompleted(Competitor competitor) {
    Collection competitors = currentCompetitors();
    return competitors.size() == 1 && competitors.iterator().next().equals(competitor);
  }

  @Override
  public double getCompletion(Competitor competitor) {
    return 0;
  }

  @Override
  public boolean isIncremental() {
    return false;
  }
}
