package net.avicus.atlas.component.visual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.event.match.MatchOpenEvent;
import net.avicus.atlas.event.player.PlayerJoinDelayedEvent;
import net.avicus.atlas.map.AtlasMap;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.StringUtil;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.UnlocalizedComponent;
import net.avicus.compendium.utils.Strings;
import net.avicus.magma.module.ListenerModule;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;

/**
 * General utility to send information about the currently playing map to users.
 */
public class MapNotificationComponent implements ListenerModule {

  private List<UnlocalizedComponent> infoCache = new ArrayList<>();

  /**
   * Get a set of {@link BaseComponent}s with information about the supplied map. The components are
   * colored so that all messages are standard. The components are ready for display without
   * modification.
   *
   * @param map to getFirst info for
   * @return a list of {@link BaseComponent}s that are: 1. Map Name 2. Map Authors (using {@link
   * StringUtil#listToEnglishCompound(Collection)}) 3. Map Version (with "()") 4. Map Game Types
   * (using {@link StringUtil#listToEnglishCompound(Collection)})
   */
  private List<UnlocalizedComponent> getMapInfo(AtlasMap map) {
    // Name
    BaseComponent name = new TextComponent(map.getName());
    name.setColor(ChatColor.AQUA);

    // Authors
    List<String> authorNames = map.getAuthors().stream()
        .map(author -> ChatColor.GREEN + author.getName() + ChatColor.AQUA)
        .collect(Collectors.toList());
    String authorString = StringUtil.listToEnglishCompound(authorNames);
    BaseComponent authors = new TextComponent(authorString);

    // Version
    BaseComponent version = new TextComponent(map.getVersion().toString());
    version.setColor(ChatColor.DARK_AQUA);
    version.setItalic(true);

    // Game Types
    List<String> types = map.getGameTypes().stream().map(gameType ->
        gameType.getName().with(org.bukkit.ChatColor.BLUE).translate(Locale.ENGLISH).toLegacyText()
            + ChatColor.AQUA).collect(Collectors.toList());
    BaseComponent gameType = new TextComponent(StringUtil.listToEnglishCompound(types));
    gameType.setColor(ChatColor.BLUE);

    return Arrays.asList(new UnlocalizedComponent(name), new UnlocalizedComponent(authors),
        new UnlocalizedComponent(version), new UnlocalizedComponent(gameType));
  }

  @Override
  public void enable() {
    AtlasTask.of(() -> {
      Match match = Atlas.getMatch();
      if (match != null) {
        match.broadcast(this.currentlyPlaying(this.infoCache));
      }
    }).repeat(20 * 100, 20 * 60);
  }

  @EventHandler
  public void onOpen(MatchOpenEvent event) {
    this.infoCache.clear();
    this.infoCache.addAll(this.getMapInfo(event.getMatch().getMap()));
  }

  @EventHandler
  public void onJoin(PlayerJoinDelayedEvent event) {
    if (Atlas.getMatch() == null && !this.infoCache.isEmpty()) {
      return;
    }

    event.getPlayer().sendMessage(Strings.blankLine(org.bukkit.ChatColor.GRAY));
    this.welcome(this.infoCache).forEach(event.getPlayer()::sendMessage);
    event.getPlayer().sendMessage(Strings.blankLine(org.bukkit.ChatColor.GRAY));
  }

  private Localizable currentlyPlaying(List<UnlocalizedComponent> info) {
    UnlocalizedComponent name = info.get(0);
    UnlocalizedComponent authors = info.get(1);
    UnlocalizedComponent version = info.get(2);

    return Messages.UI_CURRENTLY_PLAYING.with(org.bukkit.ChatColor.RED, name, version, authors);
  }

  private List<Localizable> welcome(List<UnlocalizedComponent> info) {
    UnlocalizedComponent name = info.get(0);
    UnlocalizedComponent authors = info.get(1);
    UnlocalizedComponent version = info.get(2);
    UnlocalizedComponent types = info.get(3);

    return Arrays.asList(Messages.UI_WELCOME_LINE_1.with(org.bukkit.ChatColor.GOLD, name, version),
        Messages.UI_WELCOME_LINE_2.with(org.bukkit.ChatColor.GOLD, authors, types));
  }
}
