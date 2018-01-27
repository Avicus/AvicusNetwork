package net.avicus.atlas.map.library.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.map.library.MapSource;
import org.apache.commons.io.FileUtils;

/**
 * A map folder located on disk.
 */
@ToString(exclude = "library")
public class LocalMapSource implements MapSource {

  /**
   * Library that the source is a part of.
   */
  @Getter
  private final LocalMapLibrary library;
  /**
   * Directory containing the source files.
   */
  @Getter
  private final File folder;
  /**
   * The source's {@code map.xml} file.
   */
  @Getter
  private final File xmlFile;

  /**
   * Constructor.
   *
   * @param library library that the source is a part of
   * @param folder directory containing the source files
   * @param xml the source's {@code map.xml} file
   */
  public LocalMapSource(LocalMapLibrary library, File folder, File xml) {
    this.library = library;
    this.folder = folder;
    this.xmlFile = xml;
  }

  @Override
  public String getName() {
    return this.folder.getName();
  }

  @Override
  public InputStream getXml() throws Exception {
    return new FileInputStream(this.xmlFile);
  }

  @Override
  public InputStream getFile(String path) throws FileNotFoundException {
    return new FileInputStream(new File(this.folder, path));
  }

  @Override
  public void copyWorld(File path) throws IOException {
    FileUtils.copyDirectory(this.folder, path);
  }

  @Override
  public void copyWorld(File path, String subDir) throws IOException {
    FileUtils.copyDirectory(new File(this.folder.getAbsolutePath() + "/" + subDir), path);
    FileUtils.copyFileToDirectory(this.xmlFile, path);
  }
}
