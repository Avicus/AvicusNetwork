package net.avicus.atlas.module.elimination.executor;

import java.util.UUID;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.elimination.EliminationModule;
import net.avicus.atlas.module.executors.Executor;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.compendium.number.NumberAction;
import net.avicus.compendium.number.PreparedNumberAction;
import org.bukkit.entity.Player;

/**
 * An executor that is used to modify the amount of lives a player has remaining.
 */
public class ModifyLivesExecutor extends Executor {

  private final PreparedNumberAction action;
  private final EliminationModule module;

  public ModifyLivesExecutor(String id, Check check, PreparedNumberAction action,
      EliminationModule module) {
    super(id, check);
    this.action = action;
    this.module = module;
  }

  public static Executor parse(Match match, XmlElement element) throws XmlException {
    Check check = FactoryUtils
        .resolveRequiredCheckChild(match, element.getAttribute("check"), element.getChild("check"));
    String id = element.getAttribute("id").asString().orElse(UUID.randomUUID().toString());
    NumberAction action = element.getAttribute("action").asRequiredNumberAction();
    int by = element.getAttribute("amount").asInteger().orElse(1);

    return new ModifyLivesExecutor(id, check, new PreparedNumberAction(by, action),
        match.getRequiredModule(EliminationModule.class));
  }

  @Override
  public void execute(CheckContext context) {
    Player player = context.getFirst(PlayerVariable.class).map(PlayerVariable::getPlayer)
        .orElse(null);
    if (player != null) {
      module.setLives(player, action.perform(module.getLives(player)));
    }
  }
}
