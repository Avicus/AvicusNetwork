package net.avicus.atlas.module.world;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.EnumAttribute;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.executors.ExecutorsFactory;
import net.avicus.atlas.util.xml.XmlElement;
import org.bukkit.Difficulty;
import org.bukkit.WorldType;

public class WorldFactory implements ModuleFactory<WorldModule> {

  static {
    ExecutorsFactory.registerDocumentation(ChangeTimeExecutor::documentation);
  }

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .category(ModuleDocumentation.ModuleCategory.MISC)
        .name("World Modification")
        .tagName("world")
        .description(
            "This module can be used to configure different options about the match world.")
        .feature(FeatureDocumentation.builder()
            .name("World Path")
            .description(
                "Use this to change the location that Atlas should load the world files from.")
            .description(
                "This is useful when combined with conditionals so that different worlds load based on variables.")
            .attribute("path", new GenericAttribute(String.class, true,
                "The path to the level.dat and region files."))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Game Rules")
            .tagName("gamerules")
            .description("Use this to modify game rules for the match world.")
            .attribute("TAG NAME",
                new EnumAttribute(GameRule.class, true, "The game rule to be modified"))
            .text(new GenericAttribute(String.class, true,
                "The value that the game rule should be set to.",
                "This directly mirrors the /gamerule command"))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Mob Control")
            .tagName("mobs")
            .description("This is used to control which mobs spawn and how they can be spawned.")
            .attribute("check", Attributes.check(true, "before a mob spawns"))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Weather Control")
            .tagName("weather")
            .description("This is used to control when the weather state of the world can change.")
            .attribute("check", Attributes.check(true, "before the weather changes"))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Storm Control")
            .tagName("storm")
            .description("This is used to set the initial weather state to stormy or clear.")
            .text(new GenericAttribute(Boolean.class, true,
                "If the world should start raining/snowing"))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("World Difficulty")
            .tagName("difficulty")
            .description("This is used to set the difficulty of the match world.")
            .description("Difficulty affects hunger, mob damage, etc.")
            .text(new EnumAttribute(Difficulty.class, true, "The difficulty of the match world."))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("World Type")
            .tagName("type")
            .description("Use this to set the type of the match world.")
            .description("This should not be confused with biomes.")
            .description(
                "The advantage of using the flat world type is that there is no black horizon line.")
            .text(new EnumAttribute(WorldType.class, true, "The type of the match world."))
            .build())
        .build();
  }

  @Override
  public Optional<WorldModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    Map<GameRule, String> gamerules = new HashMap<>();

    Optional<Check> mobs = Optional.empty();

    Optional<Check> weather = Optional.empty();

    Optional<Difficulty> difficulty = Optional.empty();

    WorldType type = WorldType.FLAT;

    Optional<String> path = Optional.empty();

    List<XmlElement> elements = root.getChildren("world");

    boolean shouldStorm = false;

    if (!elements.isEmpty()) {
      for (XmlElement element : elements) {
        // path=""
        if (element.hasAttribute("path")) {
          path = element.getAttribute("path").asString();
        }

        // <gamerules>
        if (element.hasChild("gamerules")) {
          XmlElement el = element.getChild("gamerules").get();
          for (XmlElement child : el.getChildren()) {
            GameRule rule = GameRule.valueOf(child.getName());
            String value = child.getText().asRequiredString();
            gamerules.put(rule, value);
          }
        }

        // <mobs>
        if (element.hasChild("mobs")) {
          XmlElement el = element.getChild("mobs").get();
          Check check = FactoryUtils.resolveRequiredCheckChild(match, el.getAttribute("check"), el);
          mobs = Optional.of(check);
        }

        // <weather>
        if (element.hasChild("weather")) {
          XmlElement el = element.getChild("weather").get();
          Check check = FactoryUtils.resolveRequiredCheckChild(match, el.getAttribute("check"), el);
          weather = Optional.of(check);
        }

        // <storm>
        if (element.hasChild("storm")) {
          shouldStorm = element.getRequiredChild("storm").getText().asRequiredBoolean();
        }

        // <difficulty>
        if (element.hasChild("difficulty")) {
          XmlElement el = element.getChild("difficulty").get();
          difficulty = el.getAttribute("difficulty").asEnum(Difficulty.class, true);
        }

        // <type>
        if (element.hasChild("type")) {
          XmlElement el = element.getChild("type").get();
          type = el.getText().asRequiredEnum(WorldType.class, true);
        }
      }
    }

    return Optional
        .of(new WorldModule(match, gamerules, mobs, difficulty, weather, type, path, shouldStorm));
  }

}
