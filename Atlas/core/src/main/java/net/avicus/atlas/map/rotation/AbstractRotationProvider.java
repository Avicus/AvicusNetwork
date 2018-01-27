package net.avicus.atlas.map.rotation;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.avicus.atlas.map.AtlasMap;
import net.avicus.atlas.map.MapManager;
import net.avicus.atlas.map.library.MapLibrary;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public abstract class AbstractRotationProvider implements RotationProvider {

  protected final MatchFactory factory;
  private final MapManager mm;

  AbstractRotationProvider(MapManager mm, final MatchFactory factory) {
    this.mm = mm;
    this.factory = factory;
  }

  Rotation defineRotation(List<Match> maps) {
    if (maps.isEmpty()) {
      this.error(Messages.ERROR_NO_MAPS, null);
      throw new IllegalStateException("No maps provided in rotation.");
    }
    return new Rotation(maps);
  }

  @Nullable
  Match createMatch(AtlasMap map) {
    try {
      return this.factory.create(map);
    } catch (Exception e) {
      this.error(Messages.ERROR_PARSING_FAILED, map.getName());
      e.printStackTrace();
    }
    return null;
  }

  @Nullable
  Match createMatch(String name) {
    final Optional<AtlasMap> map = MapLibrary.search(name, this.mm.getLibraries());
    if (!map.isPresent()) {
      this.error(Messages.ERROR_MAP_NOT_FOUND, name);
      return null;
    }
    return this.createMatch(map.get());
  }

  void error(LocalizedFormat translation, @Nullable String map) {
    final Localizable message = map != null ? translation.with(map) : translation.with();
    message.style().color(ChatColor.RED);
    Bukkit.getConsoleSender().sendMessage(message);
  }
}
