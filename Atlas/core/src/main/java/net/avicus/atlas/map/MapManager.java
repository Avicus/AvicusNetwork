package net.avicus.atlas.map;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.avicus.atlas.map.library.MapLibrary;
import net.avicus.atlas.map.library.fs.LocalMapLibrary;
import net.avicus.atlas.map.library.github.GitHubMapLibrary;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.config.Config;
import net.avicus.compendium.locale.text.Localizable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class MapManager {

  private final List<MapLibrary> libraries = new ArrayList<>();

  public List<MapLibrary> getLibraries() {
    return this.libraries;
  }

  public List<? extends AtlasMap> getMaps() {
    return this.libraries.stream().flatMap(l -> l.getMaps().stream()).collect(Collectors.toList());
  }

  public Optional<AtlasMap> search(String name) {
    return MapLibrary.search(name, this.libraries);
  }

  public void loadLibraries(List<Config> libraries) {
    for (Config section : libraries) {
      boolean github = section.getBoolean("github", false);
      String path = section.getString("path");
      List<String> ignored = new ArrayList<>();
      if (section.contains("ignored-directories")) {
        ignored.addAll(section.getStringList("ignored-directories"));
      }

      MapLibrary library;

      if (github) {
        library = new GitHubMapLibrary(path.split("/")[0], path.split("/")[1], ignored);
      } else {
        library = new LocalMapLibrary(new File(path), ignored);
      }

      if (!library.exists()) {
        Localizable message = Messages.ERROR_NO_LIBRARY.with(library.getName());
        message.style().color(ChatColor.RED);
        Bukkit.getConsoleSender().sendMessage(message);
        continue;
      }

      library.build();
      this.libraries.add(library);
    }
  }
}
