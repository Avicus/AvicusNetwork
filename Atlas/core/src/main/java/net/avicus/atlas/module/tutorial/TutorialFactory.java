package net.avicus.atlas.module.tutorial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.loadouts.LoadoutsFactory;
import net.avicus.atlas.module.loadouts.type.ItemLoadout;
import net.avicus.atlas.module.locales.LocalesModule;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.LocalizedXmlTitle;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.tutorial.api.Tutorial;
import net.avicus.tutorial.api.TutorialStep;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.joda.time.Duration;

public class TutorialFactory implements ModuleFactory<TutorialModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .category(ModuleDocumentation.ModuleCategory.CORE)
        .name("Map Tutorials")
        .tagName("tutorial")
        .description(
            "Tutorials are used to give players an introduction to the map with use of text and locations.")
        .feature(FeatureDocumentation.builder()
            .name("Tutorial Step")
            .tagName("step")
            .description("A tutorial contains information about the map,")
            .attribute("freeze", new GenericAttribute(Boolean.class, false,
                "If the player should be frozen in place when they are in this step."), true)
            .attribute("clear-inventory", new GenericAttribute(Boolean.class, false,
                "If the player's inventory should be cleared when they are in this step."), true)
            .attribute("fly", new GenericAttribute(Boolean.class, false,
                "If the player should be allowed to fly when they are in this step."), true)
            .attribute("countdown", Attributes.duration(false, true,
                "How long this step should last before advancing to the next step."))
            .attribute("yaw", new GenericAttribute(Float.class, false,
                "The yaw that the player should be at when in this step."))
            .attribute("pitch", new GenericAttribute(Float.class, false,
                "The pitch that the player should be at when in this step."))
            .attribute("location",
                Attributes.vector(false, "The location that the step should start at."))
            .subFeature(FeatureDocumentation.builder()
                .name("Chat")
                .tagName("chat")
                .description(
                    "This is used to specify lines of text that the player should see in chat when this step starts.")
                .text(new GenericAttribute(LocalizedXmlString.class, true,
                    "The text that is on this line."))
                .build())
            .subFeature(FeatureDocumentation.builder()
                .name("Title")
                .tagName("title")
                .description(
                    "This is used to specify lines of text that the player should see as a title when this step starts.")
                .attribute("text", new GenericAttribute(LocalizedXmlString.class, false,
                    "The text of the main title."))
                .attribute("subtitle", new GenericAttribute(LocalizedXmlString.class, false,
                    "The text of the subtitle."))
                .attribute("fade-in", new GenericAttribute(Integer.class, false,
                    "If the title location is used, The time if takes the title to fade in."), 20)
                .attribute("stay", new GenericAttribute(Integer.class, false,
                        "If the title location is used, The time if takes the title to stay on screen."),
                    40)
                .attribute("fade-out", new GenericAttribute(Integer.class, false,
                    "If the title location is used, The time if takes the title to fade out."), 20)
                .build())
            .subFeature(FeatureDocumentation.builder()
                .name("Inventory")
                .tagName("inventory")
                .description(
                    "This is used to give a player certain items when they are in this step.")
                .attribute("items",
                    Attributes.loadout(true, "items", "Items that should be given to the player."))
                .build())
            .build())
        .build();
  }

  @Override
  public Optional<TutorialModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<XmlElement> rootElements = root.getChildren("tutorial");

    if (rootElements.isEmpty()) {
      return Optional.empty();
    }

    String name = UUID.randomUUID().toString();

    List<XmlElement> elements = new ArrayList<>();
    rootElements.forEach(element -> {
      if (element.hasChild("prefix")) {
        elements.addAll(element.getChildren("prefix"));
      }
      elements.addAll(element.getChildren("step"));
      if (element.hasChild("suffix")) {
        elements.addAll(element.getChildren("suffix"));
      }
    });

    List<TutorialStep> steps = elements.stream()
        .map((el) -> parseTutorialStep(match, factory, el))
        .collect(Collectors.toList());

    TutorialModule module = new TutorialModule(match, new Tutorial(Optional.empty(), name, steps));
    return Optional.of(module);
  }

  private MatchTutorialStep parseTutorialStep(Match match, MatchFactory factory,
      XmlElement element) {
    LocalesModule locales = match.getRequiredModule(LocalesModule.class);

    boolean freeze = element.getAttribute("freeze").asBoolean().orElse(true);
    boolean clearInventory = element.getAttribute("clear-inventory").asBoolean().orElse(true);
    boolean fly = element.getAttribute("fly").asBoolean().orElse(true);

    Optional<Double> countdown = element.getAttribute("countdown").asDuration()
        .map((dur) -> (double) dur.getMillis() / 1000.0);

    Optional<Vector> location = element.getAttribute("location").asVector();
    Optional<Float> yaw = element.getAttribute("yaw").asNumber().map(Number::floatValue);
    Optional<Float> pitch = element.getAttribute("pitch").asNumber().map(Number::floatValue);

    Optional<List<XmlElement>> lines = element.getChild("chat").map(XmlElement::getChildren);

    Optional<List<LocalizedXmlString>> chat = lines
        .map((el) -> el.stream()
            .map((line) -> locales.parse(line.getText().asRequiredString()))
            .collect(Collectors.toList()));

    Optional<LocalizedXmlTitle> title = Optional.empty();

    if (element.hasChild("title")) {
      XmlElement el = element.getChild("title").get();

      String titleText = el.getAttribute("text").asString().orElse("");
      String subtitleText = el.getAttribute("subtitle").asString().orElse("");

      LocalizedXmlString titleLocalized = locales.parse(titleText);
      LocalizedXmlString subtitleLocalized = locales.parse(subtitleText);

      int fadeIn = (int) Math.floor(
          el.getAttribute("fade-in").asDuration().orElse(Duration.millis(500)).getMillis() / 50.0);
      int stay = (int) Math.floor(
          el.getAttribute("stay").asDuration().orElse(Duration.millis(1000)).getMillis() / 50.0);
      int fadeOut = (int) Math.floor(
          el.getAttribute("fade-out").asDuration().orElse(Duration.millis(500)).getMillis() / 50.0);

      title = Optional
          .of(new LocalizedXmlTitle(titleLocalized, subtitleLocalized, fadeIn, stay, fadeOut));
    }

    Optional<Map<Integer, ItemStack>> inventory = Optional.empty();

    if (element.hasChild("inventory")) {
      XmlElement el = element.getChild("inventory").get();

      Map<Integer, ItemStack> items = new HashMap<>();
      inventory = Optional.of(items);

      ItemLoadout loadout = factory.getFactory(LoadoutsFactory.class)
          .parseItemLoadout(match, el, true, null);
      for (int slot : loadout.getSlotedItems().keySet()) {
        items.put(slot, loadout.getSlotedItems().get(slot).getBaseItemStack());
      }
    }

    return new MatchTutorialStep(freeze, clearInventory, fly, countdown, location, yaw, pitch, chat,
        title, inventory);
  }
}
