package net.avicus.atlas.map.library;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import net.avicus.atlas.map.AtlasMap;

/**
 * A mao library is a collection of {@link MapSource}s within a root location. Atlas can pull from
 * multiple libraries at once, but identical maps from different sources are not allowed.
 */
public interface MapLibrary {

  /**
   * Search for a map source by name in a list of libraries.
   *
   * @param name name of the source to search for
   * @param libraries libraries to search inside of
   * @return a map source matching the name
   */
  static Optional<AtlasMap> search(String name, List<MapLibrary> libraries) {
    for (MapLibrary library : libraries) {
      Optional<AtlasMap> result = library.search(name);
      if (result.isPresent()) {
        return result;
      }
    }
    return Optional.empty();
  }

  /**
   * Name of the library.
   *
   * @return name of the library
   */
  String getName();

  /**
   * Map sources that are inside of this library.
   *
   * @return map sources that are inside of this library
   */
  List<? extends AtlasMap> getMaps();

  /**
   * Get a file from inside of the library.
   *
   * @param path path of the file to be retrieved
   * @return a file from inside of the library
   * @throws FileNotFoundException if the file cannot be found
   */
  InputStream getFileStream(String path) throws FileNotFoundException;

  /**
   * Get a file from inside of the library.
   *
   * @param path path of the file to be retrieved
   * @return a file from inside of the library
   * @throws FileNotFoundException if the file cannot be found
   */
  File getFile(String path) throws FileNotFoundException;

  /**
   * If the library's root location exists and can be accessed.
   *
   * @return if the library's root location exists and can be accessed
   */
  boolean exists();

  /**
   * Load map sources into the library and perform any needed setup.
   */
  void build();

  /**
   * Search for a map source by name.
   *
   * @param name name of the source to search for
   * @return a map source matching the name
   */
  default Optional<AtlasMap> search(String name) {
    name = name.toLowerCase();
    AtlasMap closest = null;
    for (AtlasMap map : this.getMaps()) {
      String check = map.getName().toLowerCase();
      if (check.equals(name)) {
        return Optional.of(map);
      } else if (check.startsWith(name)) {
        closest = map;
      }
    }
    if (closest == null) {
      for (AtlasMap map : this.getMaps()) {
        String check = map.getSource().getName().toLowerCase();
        if (check.equals(name)) {
          return Optional.of(map);
        } else if (check.startsWith(name)) {
          closest = map;
        }
      }
    }
    return Optional.ofNullable(closest);
  }
}
