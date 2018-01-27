package net.avicus.atlas.map.library;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import net.avicus.atlas.match.MatchBuildException;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;

/**
 * A map source is the location that a map's world files and XML are stored.
 */
public interface MapSource {

  /**
   * Library that this source is a part of.
   *
   * @return library that this source is a part of
   */
  MapLibrary getLibrary();

  /**
   * Name of this source.
   *
   * @return name of this source
   */
  String getName();

  /**
   * Get the XML that should be used for parsing with this source's world files.
   *
   * @return the XML that should be used for parsing with this source's world files
   * @throws Exception if the file cannot be found
   */
  InputStream getXml() throws Exception;

  default Document createDocument() throws MatchBuildException {
    final SAXBuilder sax = new SAXBuilder();
    sax.setJDOMFactory(new LocatedJDOMFactory());

    final org.jdom2.Document document;

    try (final InputStream is = this.getXml()) {
      document = sax.build(is);
    } catch (Exception e) {
      throw new MatchBuildException(
          "An exception occurred while parsing XML for map '" + this.getName() + '\'', e);
    }

    return document;
  }

  /**
   * Retrieve a file from this source.
   *
   * @param path path of the file
   * @return a file from this source
   * @throws FileNotFoundException if the file cannot be found
   */
  InputStream getFile(String path) throws FileNotFoundException;

  /**
   * Copy files from this source to a folder.
   *
   * @param path path to copy the files to.\
   * @throws IOException if the files cannot be copied
   */
  void copyWorld(File path) throws IOException;

  /**
   * Copy files from a subdirectory in this source to a folder.
   * This will also copy a {@code map.xml} from the source's root.
   *
   * @param path path to copy the files to
   * @param subName folder name to copy files from
   * @throws IOException if the files cannot be copied
   */
  void copyWorld(File path, String subName) throws IOException;
}
