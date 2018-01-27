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
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import org.bukkit.entity.Player;

/**
 * An executor that executes all sub-executors on every player of a {@link Group}.
 */
@ToString
public class GroupExecutor extends Executor {

  private final Group group;
  private final Executor executor;

  public GroupExecutor(String id, Check check, Group group, Executor executor) {
    super(id, check);
    this.group = group;
    this.executor = executor;
  }

  public static FeatureDocumentation documentation() {
    return FeatureDocumentation.builder()
        .name("Perform Executor(s) On Group")
        .tagName("perform-on-group")
        .description("An executor that is used to perform executors on an entire group of players.")
        .attribute("group", Attributes.idOf(true, "group to perform the executor on"))
        .attribute("execute", Attributes.idOf(true, "executor"))
        .build();
  }

  public static Executor parse(Match match, XmlElement element) throws XmlException {
    Check check = FactoryUtils
        .resolveRequiredCheckChild(match, element.getAttribute("check"), element.getChild("check"));
    String id = element.getAttribute("id").asString().orElse(UUID.randomUUID().toString());
    Group group = match.getRegistry()
        .get(Group.class, element.getAttribute("group").asRequiredString(), true).orElse(null);
    if (group == null) {
      throw new XmlException(element, "Group not found!");
    }

    return new GroupExecutor(id,
        check,
        group,
        match.getRegistry()
            .get(Executor.class, element.getAttribute("execute").asRequiredString(), true).get());
  }

  @Override
  public void execute(CheckContext context) {
    for (Player player : this.group.getPlayers()) {
      CheckContext subContext = context.duplicate();

      subContext.add(new PlayerVariable(player));

      this.executor.executeChecked(subContext);
    }
  }
}
