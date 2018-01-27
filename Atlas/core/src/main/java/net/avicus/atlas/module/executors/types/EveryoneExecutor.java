package net.avicus.atlas.module.executors.types;

import java.util.UUID;
import lombok.ToString;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
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
 * An executor that executes all sub-executors on every player.
 */
@ToString
public class EveryoneExecutor extends Executor {

  private final Executor executor;

  public EveryoneExecutor(String id, Check check, Executor executor) {
    super(id, check);
    this.executor = executor;
  }

  public static Executor parse(Match match, XmlElement element) throws XmlException {
    Check check = FactoryUtils
        .resolveRequiredCheckChild(match, element.getAttribute("check"), element.getChild("check"));
    String id = element.getAttribute("id").asString().orElse(UUID.randomUUID().toString());

    return new EveryoneExecutor(id, check, match.getRegistry()
        .get(Executor.class, element.getAttribute("execute").asRequiredString(), true).get());
  }

  public static FeatureDocumentation documentation() {
    return FeatureDocumentation.builder()
        .name("Perform Executor(s) On All Players")
        .tagName("perform-on-everyone")
        .description(
            "An executor that is used to perform executors on an all the players currently on the server, including spectators.")
        .attribute("execute", Attributes.idOf(true, "executor"))
        .build();
  }

  @Override
  public void execute(CheckContext context) {
    for (Player player : context.getMatch().getPlayers()) {
      CheckContext subContext = context.duplicate();

      subContext.add(new PlayerVariable(player));

      this.executor.executeChecked(subContext);
    }
  }
}
