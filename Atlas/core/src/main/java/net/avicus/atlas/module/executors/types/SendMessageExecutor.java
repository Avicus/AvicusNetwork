package net.avicus.atlas.module.executors.types;

import java.util.UUID;
import lombok.ToString;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.InfoTable;
import net.avicus.atlas.documentation.attributes.EnumAttribute;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.VariableUtils;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.executors.Executor;
import net.avicus.atlas.module.locales.LocalesModule;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

/**
 * An executor that sends a message to a player.
 * Can send titles, subtitles, and chat messages
 */
@ToString
public class SendMessageExecutor extends Executor {

  private final MessageLocation location;
  private final LocalizedXmlString message;
  private final int titleFadeIn;
  private final int titleHold;
  private final int titleFadeOut;

  public SendMessageExecutor(String id, Check check, MessageLocation location,
      LocalizedXmlString message, int titleFadeIn, int titleHold, int titleFadeOut) {
    super(id, check);
    this.location = location;
    this.message = message;
    this.titleFadeIn = titleFadeIn;
    this.titleHold = titleHold;
    this.titleFadeOut = titleFadeOut;
  }

  public static Executor parse(Match match, XmlElement element) throws XmlException {
    Check check = FactoryUtils
        .resolveRequiredCheckChild(match, element.getAttribute("check"), element.getChild("check"));
    String id = element.getAttribute("id").asString().orElse(UUID.randomUUID().toString());
    MessageLocation location = element.getAttribute("location")
        .asRequiredEnum(MessageLocation.class, true);
    LocalizedXmlString message = match.getRequiredModule(LocalesModule.class)
        .parse(element.getText().asRequiredString());
    int titleFadeIn = element.getAttribute("fade-in").asInteger().orElse(20);
    int titleHold = element.getAttribute("hold").asInteger().orElse(40);
    int titleFadeOut = element.getAttribute("fade-out").asInteger().orElse(20);

    return new SendMessageExecutor(id, check, location, message, titleFadeIn, titleHold,
        titleFadeOut);
  }

  public static FeatureDocumentation documentation() {
    return FeatureDocumentation.builder()
        .name("Send Message")
        .tagName("send-message")
        .description("An executor that is used to send messages to players.")
        .description(
            "The message supports contextual attributes based on the actor which are surrounded with [] (square brackets)")
        .attribute("location", new EnumAttribute(SendMessageExecutor.MessageLocation.class, true,
            "Location on the screen to send the message."))
        .attribute("message", new GenericAttribute(String.class, true, "Message to send."))
        .attribute("fade-in", new GenericAttribute(Integer.class, false,
            "If the title location is used, The time if takes the title to fade in."), 20)
        .attribute("hold", new GenericAttribute(Integer.class, false,
            "If the title location is used, The time if takes the title to stay on screen."), 40)
        .attribute("fade-out", new GenericAttribute(Integer.class, false,
            "If the title location is used, The time if takes the title to fade out."), 20)
        .table(new InfoTable("Base prefixes and definitions", "name", "description")
            .row("attacker",
                "Prefix used to identify the attacker in the context. This is effectively a sub-context which can contain any below variables.")
            .row("victim",
                "Prefix used to identify the victim in the context. This is effectively a sub-context which can contain any below variables.")
            .row("entity", "Prefix used to identify the entity in the context.")
            .row("group", "Prefix used to identify the group in the context.")
            .row("item", "Prefix used to identify the item in the context.")
            .row("location", "Prefix used to identify the location in the context.")
            .row("material", "Prefix used to identify the material in the context.")
            .row("player", "Prefix used to identify the player in the context.")
            .row("spawn-reason", "Prefix used to identify the spawn-reason in the context.")
        )
        .table(new InfoTable("Attributes that apply to vectors (velocities, locations)", "Name",
            "Description")
            .row("X", "X coordinate of the vector with no decimals.")
            .row("Y", "Y coordinate of the vector with no decimals.")
            .row("Z", "Z coordinate of the vector with no decimals.")
            .row("X-precise", "X coordinate of the vector with decimals.")
            .row("Y-precise", "Y coordinate of the vector with decimals.")
            .row("Z-precise", "Z coordinate of the vector with decimals.")
            .row("full", "X, Y, Z coordinates of the vector with no decimals.")
            .row("full-precise", "X, Y, Z coordinates of the vector with decimals.")
        )
        .table(new InfoTable("Attributes that apply to items", "Name", "Description")
            .row("type", "Type of the item.")
            .row("amount", "Amount of the item.")
            .row("damage", "Damage value of the item.")
            .row("name", "Custom name of the item.")
            .row("lore", "Lore of the item.")
        )
        .table(new InfoTable("Attributes that apply to groups", "Name", "Description")
            .row("name", "Name of the group.")
            .row("color", "Color of the group.")
            .row("min", "Minimum number of players for the group.")
            .row("max", "Maximum number of players for the group.")
            .row("overfill", "Maximum overfill of players for the group.")
            .row("size", "Number of players in the group.")
        )
        .table(new InfoTable("Attributes that apply to entities", "Name", "Description")
            .row("name", "Name of the entity.")
            .row("type", "Type of the entity")
            .row("velocity",
                "Base prefix for the velocity of the entity that is used to access the vector attributes.")
            .row("location",
                "Base prefix for the location of the entity that is used to access the vector attributes.")
        )
        .table(new InfoTable("Attributes that apply to players", "Name", "Description")
            .row("name", "Name of the player.")
            .row("velocity",
                "Base prefix for the velocity of the player that is used to access the vector attributes.")
            .row("location",
                "Base prefix for the location of the player that is used to access the vector attributes.")
            .row("holding",
                "Base prefix for item the player player is holding that is used to access the item attributes.")
            .row("health", "Health of the player.")
            .row("max-health", "Max health of the player.")
            .row("food", "Food level of the player.")
            .row("saturation", "Saturation level of the player.")
            .row("exp", "Experience level of the player.")
            .row("walk-speed", "Walk speed of the player.")
            .row("fly-speed", "Fly speed of the player.")
        )
        .build();
  }

  @Override
  public void execute(CheckContext context) {
    Player player = context.getLast(PlayerVariable.class).map(PlayerVariable::getPlayer)
        .orElse(null);
    if (player != null) {
      BaseComponent message = VariableUtils
          .replaceString(this.message.toText(), player.getLocale(), context);
      switch (this.location) {
        case CHAT:
          player.sendMessage(message);
          return;
        case TITLE:
          Title title = new Title(message, new TextComponent(), this.titleFadeIn, this.titleHold,
              this.titleFadeOut);
          player.sendTitle(title);
          return;
        case SUBTITLE:
          Title sub = new Title(new TextComponent(), message, this.titleFadeIn, this.titleHold,
              this.titleFadeOut);
          player.sendTitle(sub);
          return;
      }
    }
  }

  public enum MessageLocation {
    CHAT, TITLE, SUBTITLE
  }
}
