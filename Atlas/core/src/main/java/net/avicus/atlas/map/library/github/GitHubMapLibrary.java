package net.avicus.atlas.map.library.github;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.AtlasConfig;
import net.avicus.atlas.map.AtlasMap;
import net.avicus.atlas.map.AtlasMapFactory;
import net.avicus.atlas.map.library.MapLibrary;
import net.avicus.atlas.map.library.MapSource;
import net.avicus.atlas.match.MatchBuildException;
import net.avicus.atlas.util.xml.XmlException;
import org.javalite.http.Get;
import org.javalite.http.Http;

/**
 * A library of maps that is stored inside of a github repository.
 * This does not require git to be installed.
 */
@ToString
public class GitHubMapLibrary implements MapLibrary {

  /**
   * API endpoint constructor for the content path of a repository.
   */
  private static final String CONTENT_URL = "https://api.github.com/repos/%s/%s/contents";

  /**
   * Username/Orgname of the owner of the repo.
   */
  private final String owner;
  /**
   * Name of the repository.
   */
  private final String repo;
  /**
   * Ignored directory names within this library.
   */
  private final List<String> ignoredDirectories;
  /**
   * Formatted URL combining owner+repo with API endpoint syntax.
   */
  private final String contentUrl;

  /**
   * Sources parsed from the repository's contents.
   */
  @Getter
  private final List<AtlasMap> maps;

  /**
   * Constructor.
   *
   * @param owner Username/Orgname of the owner of the repo
   * @param repo name of the repository
   * @param ignoredDirectories ignored directory names within this library
   */
  public GitHubMapLibrary(String owner, String repo, List<String> ignoredDirectories) {
    this.owner = owner;
    this.repo = repo;
    this.ignoredDirectories = ignoredDirectories;
    this.contentUrl = String.format(CONTENT_URL, owner, repo);
    this.maps = new ArrayList<>();
  }

  @Override
  public InputStream getFileStream(String path) throws FileNotFoundException {
    // Todo: Do this.
    throw new UnsupportedOperationException();
  }

  @Override
  public File getFile(String path) throws FileNotFoundException {
    // Todo: Do this.
    throw new UnsupportedOperationException();
  }

  @Override
  public String getName() {
    return this.contentUrl;
  }

  @Override
  public boolean exists() {
    try {
      getJsonArray(this.contentUrl);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public void build() {
    JsonArray root = getJsonArray(this.contentUrl);
    // recursive search
    build(root);
  }

  /**
   * Recursively parse sources in the repository.
   *
   * @param folder folder that contains the items for parsing
   */
  private void build(JsonArray folder) {
    List<GithubItem> contents = getContents(folder);
    for (GithubItem file : contents) {
      if (file.getName().equals("map.xml")) {
        List<GithubItem> items = buildMap(contents);

        for (int i = 0; i < items.size(); i++) {
          if (items.get(i).isDirectory()) {
            items.remove(i);
          }
        }

        String[] pathSplit = file.getPath().split("/");
        String folderName = this.repo;
        if (pathSplit.length > 1) {
          folderName = pathSplit[pathSplit.length - 2];
        }

        Map<String, String> files = new HashMap<>();
        for (GithubItem item : items) {
          int index = item.getPath().indexOf(folderName);
          String path = item.getPath()
              .substring(index + folderName.length() + 1, item.getPath().length());
          files.put(item.getDownload(), path);
        }

        try {
          final MapSource source = new GitHubMapSource(this, folderName, file.getDownload(), files);
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
      } else if (file.isDirectory() && !ignoredDirectories.contains(file.getName())) {
        build(getJsonArray(file.getContentUrl()));
      }
    }
  }

  /**
   * Get a list of {@link GithubItem}s from a list of {@link GithubItem}s If an item that is
   * supplied is a directory, this will recursively search through directories and add items based
   * on files in the directory.
   *
   * @param contents list of items to be built
   * @return list of built items
   */
  private List<GithubItem> buildMap(List<GithubItem> contents) {
    List<GithubItem> result = new ArrayList<>();
    for (GithubItem item : contents) {
      if (item.isDirectory()) {
        result.addAll(buildMap(getContents(getJsonArray(item.getContentUrl()))));
      } else {
        result.add(item);
      }
    }
    return result;
  }

  /**
   * Parse a list of {@link GithubItem}s from a github API {@link JsonArray}
   *
   * @param folder array representing the folder of items
   * @return parsed items
   */
  private List<GithubItem> getContents(JsonArray folder) {
    List<GithubItem> files = new ArrayList<>();

    for (int i = 0; i < folder.size(); i++) {
      JsonObject object = folder.get(i).getAsJsonObject();
      String name = object.get("name").getAsString();
      String path = object.get("path").getAsString();
      boolean directory = object.get("type").getAsString().equals("dir");
      String content = object.get("url").getAsString();
      String download = null;
      if (!object.get("download_url").isJsonNull()) {
        download = object.get("download_url").getAsString();
      }

      files.add(new GithubItem(name, path, directory, content, download));
    }

    return files;
  }

  /**
   * Get a {@link JsonArray} from the github API.
   * If authentication is configured in the config, it will be used.
   *
   * @param url API url of the array
   * @return an array from the API
   */
  private JsonArray getJsonArray(String url) {
    Get get = Http.get(url);
    if (AtlasConfig.isGithubAuth()) {
      get.basic(AtlasConfig.getGithubUsername(), AtlasConfig.getGithubToken());
    }
    JsonParser parser = new JsonParser();
    return parser.parse(get.text()).getAsJsonArray();
  }

  /**
   * An item (file/directory) inside of the repository.
   */
  @Data
  public class GithubItem {

    /**
     * Name of the item.
     */
    private final String name;
    /**
     * Path to the item.
     */
    private final String path;
    /**
     * If the item is a directory.
     */
    private final boolean directory;
    /**
     * API content URL of the item.
     */
    private final String contentUrl;
    /**
     * API download URL of the item.
     */
    private final String download;
  }
}
