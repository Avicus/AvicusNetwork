package net.avicus.atlas.map.library.github;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.map.library.MapSource;
import org.apache.commons.io.FileUtils;
import org.javalite.http.Http;

/**
 * A map folder located on a remote github repository.
 */
@ToString(exclude = "library")
public class GitHubMapSource implements MapSource {

  /**
   * Library that this source is in.
   */
  @Getter
  private final GitHubMapLibrary library;
  /**
   * Name of the source folder.
   */
  @Getter
  private final String name;
  /**
   * URL of the {@code map.xml} file for this source.
   */
  @Getter
  private final String xmlUrl;
  /**
   * Files within this source.
   * In the format of {@code url -> local path}
   */
  private Map<String, String> files;

  /**
   * Constructor
   *
   * @param library library that this source is in
   * @param name name of the source folder
   * @param xmlUrl URL of the {@code map.xml} file for this source
   * @param files files within this source In the format of {@code url -> local path}
   */
  public GitHubMapSource(GitHubMapLibrary library, String name, String xmlUrl,
      Map<String, String> files) {
    this.library = library;
    this.name = name;
    this.xmlUrl = xmlUrl;
    this.files = files;
  }

  @Override
  public InputStream getXml() throws Exception {
    return Http.get(this.xmlUrl).getInputStream();
  }

  @Override
  public InputStream getFile(String path) throws FileNotFoundException {
    // Todo: Do this.
    throw new UnsupportedOperationException();
  }

  @Override
  public void copyWorld(File path) throws IOException {
    for (Map.Entry<String, String> entry : this.files.entrySet()) {
      InputStream stream = Http.get(entry.getKey()).getInputStream();
      FileUtils.copyInputStreamToFile(stream, new File(path, entry.getValue()));
    }
  }

  @Override
  public void copyWorld(File path, String subDir) throws IOException {
    for (Map.Entry<String, String> entry : this.files.entrySet()) {
      if (entry.getValue().contains(subDir) || entry.getValue().contains(".xml")) {
        InputStream stream = Http.get(entry.getKey()).getInputStream();
        FileUtils.copyInputStreamToFile(stream, new File(path, entry.getValue()));
      }
    }
  }
}
