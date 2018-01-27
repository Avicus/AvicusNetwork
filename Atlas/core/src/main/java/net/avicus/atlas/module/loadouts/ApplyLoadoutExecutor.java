package net.avicus.atlas.module.loadouts;

import java.util.UUID;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.executors.Executor;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import org.bukkit.entity.Player;

/**
 * An executor that gives a loadout to a player.
 */
@ToString(callSuper = true)
public class ApplyLoadoutExecutor extends Executor {

  private final Loadout loadout;

  public ApplyLoadoutExecutor(String id, Check check, Loadout loadout) {
    super(id, check);
    this.loadout = loadout;
  }

  public static Executor parse(Match match, XmlElement element) throws XmlException {
    Check check = FactoryUtils
        .resolveRequiredCheckChild(match, element.getAttribute("check"), element.getChild("check"));
    String id = element.getAttribute("id").asString().orElse(UUID.randomUUID().toString());
    Loadout loadout = FactoryUtils.resolveRequiredLoadout(match, element.getAttribute("loadout"),
        element.getChild("loadout"));
    return new ApplyLoadoutExecutor(id, check, loadout);
  }

  @Override
  public void execute(CheckContext context) {
    Player player = context.getFirst(PlayerVariable.class).map(PlayerVariable::getPlayer)
        .orElse(null);
    if (player != null) {
      loadout.apply(player);
    }
  }
}
