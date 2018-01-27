package net.avicus.atlas.module.world;

import java.util.UUID;
import lombok.ToString;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.executors.Executor;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.compendium.number.NumberAction;
import net.avicus.compendium.number.PreparedNumberAction;
import org.bukkit.World;


@ToString
public class ChangeTimeExecutor extends Executor {

  private final PreparedNumberAction action;

  public ChangeTimeExecutor(String id, Check check, PreparedNumberAction action) {
    super(id, check);
    this.action = action;
  }

  public static FeatureDocumentation documentation() {
    return FeatureDocumentation.builder()
        .name("Change World Time")
        .tagName("change-world-time")
        .description("This executor is used to change the time of match world.")
        .attribute("time", new GenericAttribute(Integer.class, true,
            "Amount of time (in ticks) to change the time by."))
        .attribute("action", Attributes.action(false, "the world time"), "set")
        .build();
  }

  public static Executor parse(Match match, XmlElement element) throws XmlException {
    Check check = FactoryUtils
        .resolveRequiredCheckChild(match, element.getAttribute("check"), element.getChild("check"));
    String id = element.getAttribute("id").asString().orElse(UUID.randomUUID().toString());
    NumberAction action = element.getAttribute("action").asNumberAction().orElse(NumberAction.SET);
    int by = element.getAttribute("time").asRequiredInteger();
    return new ChangeTimeExecutor(id, check, new PreparedNumberAction(by, action));
  }

  @Override
  public void execute(CheckContext context) {
    final World world = context.getMatch().getWorld();
    world.setTime(this.action.perform(world.getTime()));
  }
}
