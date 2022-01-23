package net.avicus.atlas.module.objectives.score;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.objectives.IntegerObjective;
import net.avicus.atlas.module.objectives.score.event.PointEarnEvent;
import net.avicus.atlas.module.shop.PlayerEarnPointEvent;
import net.avicus.atlas.runtimeconfig.fields.ConfigurableField;
import net.avicus.atlas.runtimeconfig.fields.OptionalField;
import net.avicus.atlas.runtimeconfig.fields.SimpleFields.IntField;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.number.NumberAction;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.entity.Player;

@ToString(exclude = "match")
public class ScoreObjective implements IntegerObjective {

  private final Match match;
  @Getter
  private Optional<Integer> limit;
  @Getter
  private final Optional<Team> team;
  @Getter
  private Optional<Integer> kills;
  @Getter
  private Optional<Integer> deaths;
  private final Map<Competitor, Integer> points;
  private Optional<LocalizedXmlString> overriddenName = Optional.empty();

  public ScoreObjective(Match match, Optional<Integer> limit, Optional<Team> team,
      Optional<Integer> kills, Optional<Integer> deaths) {
    this.match = match;
    this.limit = limit;
    this.team = team;
    this.kills = kills;
    this.deaths = deaths;
    this.points = new HashMap<>();
  }

  @Override
  public int getPoints(Competitor competitor) {
    return this.points.getOrDefault(competitor, 0);
  }

  public void modify(Competitor competitor, int amount) {
    modify(competitor, amount, null);
  }

  public void modify(Competitor competitor, int amount, @Nullable Player actor) {
    modify(competitor, amount, NumberAction.ADD, actor);
  }

  @Override
  public void modify(Competitor competitor, int amount, NumberAction action,
      @Nullable Player actor) {
    if (!canComplete(competitor)) {
      throw new IllegalArgumentException();
    }

    amount = action.perform(getPoints(competitor), amount);

    PointEarnEvent event = new PointEarnEvent(this, actor, amount);
    Events.call(event);
    Events.call(new PlayerEarnPointEvent(actor, "point-earn"));

    this.points.put(competitor, amount);
  }

  @Override
  public void initialize() {

  }

  @Override
  public LocalizedXmlString getName() {
    return this.overriddenName.orElse(new LocalizedXmlString(Messages.UI_POINTS));
  }

  @Override
  public void setName(LocalizedXmlString name) {
    this.overriddenName = Optional.of(name);
  }

  @Override
  public boolean canComplete(Competitor competitor) {
    if (this.team.isPresent()) {
      Team team = this.team.get();
      return team.equals(competitor.getGroup());
    }
    return true;
  }

  @Override
  public boolean isCompleted(Competitor competitor) {
    if (!canComplete(competitor)) {
      throw new IllegalArgumentException("Competitor cannot complete objective.");
    }
    if (!this.limit.isPresent()) {
      return false;
    }
    return getPoints(competitor) >= this.limit.get();
  }

  @Override
  public double getCompletion(Competitor competitor) {
    if (!canComplete(competitor)) {
      throw new IllegalArgumentException("Competitor cannot complete objective.");
    }
    if (!this.limit.isPresent()) {
      return 0.5;
    }
    return (double) getPoints(competitor) / Math.max(this.limit.get(), 1);
  }

  @Override
  public boolean isIncremental() {
    return true;
  }

  @Override
  public ConfigurableField[] getFields() {
    return ArrayUtils.addAll(
        IntegerObjective.super.getFields(),
        new OptionalField<>("Limit", () -> this.limit, (v) -> this.limit = v, new IntField("limit")),
        new OptionalField<>("Points Per Kill", () -> this.kills, (v) -> this.kills = v, new IntField("kills")),
        new OptionalField<>("Points Per Death", () -> this.deaths, (v) -> this.deaths = v, new IntField("deaths"))
    );
  }
}
