package net.avicus.atlas.map.library.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.map.AtlasMap;
import net.avicus.atlas.map.AtlasMapFactory;
import net.avicus.atlas.map.library.MapLibrary;
import net.avicus.atlas.map.library.MapSource;
import net.avicus.atlas.match.MatchBuildException;
import net.avicus.atlas.util.xml.XmlException;

/**
 * A library of maps that is stored on disk.
 * Atlas does not need write access to the directory.
 */
@ToString
public class LocalMapLibrary implements MapLibrary {

  /**
   * Root directory of the library.
   */
  @Getter
  private final File root;
  /**
   * Ignored directory names within this library.
   */
  @Getter
  private final List<String> ignoredDirectories;
  /**
   * Sources parsed from the root folder's contents.
   */
  @Getter
  private final List<AtlasMap> maps;

  /**
   * Constructor.
   *
   * @param root root directory of the library
   * @param ignoredDirectories ignored directory names within this library
   */
  public LocalMapLibrary(File root, List<String> ignoredDirectories) {
    this.root = root;
    this.ignoredDirectories = ignoredDirectories;
    this.maps = new ArrayList<>();
  }

  @Override
  public String getName() {
    return this.root.getAbsolutePath();
  }

  @Override
  public boolean exists() {
    return this.root.exists() && this.root.isDirectory() && this.root.canRead();
  }

  @Override
  public void build() {
    this.maps.clear();

    // recursive method
    build(this.root);
  }

  @Override
  public InputStream getFileStream(String path) throws FileNotFoundException {
    return new FileInputStream(getFile(path));
  }

  @Override
  public File getFile(String path) throws FileNotFoundException {
    return new File(this.root, path);
  }

  /**
   * Recursively parse sources in a directory.
   *
   * @param directory directory that contains the items for parsing
   */
  private void build(File directory) {
    File[] list = directory.listFiles();
    if (list == null) {
      return;
    }

    for (File file : list) {
      if (file.isDirectory() && !this.ignoredDirectories.contains(file.getName())) {
        build(file);
      } else {
        if (file.getName().equals("map.xml")) {
          try {
            final MapSource source = getMapFolder(file.getParentFile());
            final AtlasMap map = AtlasMapFactory.parse(source, source.createDocument());
            this.maps.add(map);
          } catch (Exception e) {
            if (e instanceof MatchBuildException || e instanceof XmlException) {
              if (e.getCause() != null) {
                Atlas.get().getMapErrorLogger().warning(
                    "Failed to read XML: " + file.getPath() + " " + e.getCause().getMessage());
              } else {
                Atlas.get().getMapErrorLogger()
                    .warning("Failed to read XML: " + file.getPath() + " " + e.getMessage());
              }
            } else {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }

  /**
   * Parse a map source from a folder.
   *
   * @param root folder of the source
   * @return a map source from the folder
   * @throws Exception if there was an issue loading files
   */
  private MapSource getMapFolder(File root) throws Exception {
    File xml = xml(root);
    return new LocalMapSource(this, root, xml);
  }

  /**
   * Get a {@code map.xml} from a directory
   *
   * @param root folder containing the XML
   * @return the XML
   * @throws Exception if the folder does not contain a {@code map.xml}
   */
  private File xml(File root) throws Exception {
    File[] files = root.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.getName().equals("map.xml")) {
          return file;
        }
      }
    }
    throw new Exception("config not found");
  }
}
