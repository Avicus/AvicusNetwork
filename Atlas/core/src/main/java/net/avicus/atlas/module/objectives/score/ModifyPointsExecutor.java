package net.avicus.atlas.module.objectives.score;

import java.util.UUID;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.executors.Executor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.compendium.number.NumberAction;
import org.bukkit.entity.Player;

/**
 * An executor that will modify a player's points using a value and action.
 */
public class ModifyPointsExecutor extends Executor {

  private final ObjectivesModule module;
  private final int score;
  private final NumberAction modify;
  private final GroupsModule groupsModule;

  public ModifyPointsExecutor(String id, Check check, ObjectivesModule module, int score,
      NumberAction modify) {
    super(id, check);
    this.module = module;
    this.score = score;
    this.modify = modify;
    this.groupsModule = this.module.getMatch().getRequiredModule(GroupsModule.class);
  }

  public static Executor parse(Match match, XmlElement element) throws XmlException {
    Check check = FactoryUtils
        .resolveRequiredCheckChild(match, element.getAttribute("check"), element.getChild("check"));
    String id = element.getAttribute("id").asString().orElse(UUID.randomUUID().toString());
    ObjectivesModule objectivesModule = match.getRequiredModule(ObjectivesModule.class);
    NumberAction modify = element.getAttribute("action").asNumberAction().orElse(NumberAction.ADD);
    int score = element.getAttribute("score").asRequiredInteger();
    return new ModifyPointsExecutor(id, check, objectivesModule, score, modify);
  }

  @Override
  public void execute(CheckContext context) {
    Player player = context.getFirst(PlayerVariable.class).map(PlayerVariable::getPlayer)
        .orElse(null);
    if (player != null) {
      this.groupsModule.getCompetitorOf(player).ifPresent((comp -> {
        this.module.score(comp, this.score, this.modify);
      }));
    }
  }
}
