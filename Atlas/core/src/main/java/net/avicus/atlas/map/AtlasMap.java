package net.avicus.atlas.map;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.AtlasConfig;
import net.avicus.atlas.GameType;
import net.avicus.atlas.map.library.MapSource;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchBuildException;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.elimination.EliminationModule;
import net.avicus.atlas.module.map.CountdownConfig;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.StringUtil;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.game.MinecraftMap;
import net.avicus.magma.game.author.Author;
import net.avicus.magma.util.MapGenre;
import net.avicus.magma.util.Version;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jdom2.Document;

@ToString
public class AtlasMap implements Module, MinecraftMap {

  private static final UnlocalizedFormat NAME_VERSION_FORMAT = new UnlocalizedFormat("{0} {1}");
  @Getter
  private final Version specification;
  @Getter
  private final Set<TypeDetector> detectors;
  @Getter
  private final String slug;
  @Getter
  private final String name;
  @Getter
  private final Version version;
  @Getter
  private final List<Author> authors;
  @Getter
  private final List<Author> contributors;
  @Getter
  private final EnumSet<GameType> gameTypes;
  @Getter
  private final CountdownConfig countdownConfig;
  @Getter
  private final MapSource source;
  private final ClickEvent clickEvent;
  private final UnlocalizedText clickableNameVersion;
  @Getter
  @Nullable
  private MapGenre genre;

  public AtlasMap(Set<TypeDetector> detectors, String slug, String name, Version specification,
      Version version, @Nullable MapGenre genre, List<Author> authors, List<Author> contributors,
      EnumSet<GameType> gameTypes, CountdownConfig countdownConfig, MapSource source) {
    this.detectors = detectors;
    this.slug = slug;
    this.name = name;
    this.specification = specification;
    this.version = version;
    this.genre = genre;
    this.authors = authors;
    this.contributors = contributors;
    this.gameTypes = gameTypes;
    this.countdownConfig = countdownConfig;
    this.source = source;
    this.clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL,
        AtlasConfig.Website.resolvePath(this.slug));
    this.clickableNameVersion = NAME_VERSION_FORMAT.with(new UnlocalizedText(this.name),
        new UnlocalizedText(" (" + this.version.toString() + ')',
            TextStyle.ofColor(ChatColor.GRAY).italic()));
  }

  static String slugify(String name) {
    return StringUtil.slugify(name);
  }

  public TextComponent getClickableName(CommandSender source) {
    return this.getClickableName(source, false);
  }

  public TextComponent getClickableName(CommandSender source, boolean withVersion) {
    return this.getClickableName(source.getLocale(), withVersion);
  }

  public TextComponent getClickableName(Locale source) {
    return this.getClickableName(source, false);
  }

  public TextComponent getClickableName(Locale source, boolean withVersion) {
    return this.applyClickHover(source,
        withVersion ? this.clickableNameVersion.translate(source) : new TextComponent(this.name));
  }

  private TextComponent applyClickHover(Locale source, TextComponent component) {
    component.setClickEvent(this.clickEvent);
    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
        new BaseComponent[]{Messages.UI_VIEW_MAP_ON_WEBSITE.with().translate(source)}));
    return component;
  }

  public void determineGenre(Match match) {
    if (this.genre != null) {
      return;
    }

    for (TypeDetector detector : this.detectors) {
      if (this.genre != null) {
        break;
      }

      this.genre = detector.detectGenre(match).orElse(null);
    }
    match.getModule(EliminationModule.class)
        .ifPresent(eliminationModule -> this.genre = MapGenre.ELIMINATION);

    if (this.genre == null) {
      throw new RuntimeException("Unable to determine genre.");
    }
  }

  public void detectGameTypes(Match match) {
    if (!this.gameTypes.isEmpty()) {
      return;
    }

    for (TypeDetector detector : this.detectors) {
      this.gameTypes.addAll(detector.detectGameTypes(match));
    }

    match.getModule(EliminationModule.class)
        .ifPresent(eliminationModule -> this.gameTypes.add(GameType.ELIMINATION));
  }

  public Document createDocument() throws MatchBuildException {
    return this.source.createDocument();
  }

  public interface TypeDetector {

    Optional<MapGenre> detectGenre(Match match);

    Set<GameType> detectGameTypes(Match match);
  }
}
