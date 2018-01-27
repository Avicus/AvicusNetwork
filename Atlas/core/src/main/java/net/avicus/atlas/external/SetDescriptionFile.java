package net.avicus.atlas.external;

import com.google.common.collect.ImmutableList;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

/**
 * Class to represent a file that hold asic information about an external module.
 */
@Getter
public class SetDescriptionFile {

  private static final Yaml yaml = new Yaml(new SafeConstructor());
  private String main = null;
  private String name = null;
  private List<String> depend = null;

  public SetDescriptionFile(final InputStream stream) throws Exception {
    loadMap(asMap(yaml.load(stream)));
  }

  public SetDescriptionFile(final Reader reader) throws Exception {
    loadMap(asMap(yaml.load(reader)));
  }

  private void loadMap(Map<?, ?> map) throws Exception {
    try {
      name = map.get("name").toString();
    } catch (NullPointerException ex) {
      throw new Exception("name is not defined", ex);
    } catch (ClassCastException ex) {
      throw new Exception("name is of wrong type", ex);
    }

    try {
      main = map.get("main").toString();
    } catch (NullPointerException ex) {
      throw new Exception("main is not defined", ex);
    } catch (ClassCastException ex) {
      throw new Exception("main is of wrong type", ex);
    }

    if (map.get("depend") != null) {
      ImmutableList.Builder<String> dependBuilder = ImmutableList.<String>builder();
      try {
        for (Object dependency : (Iterable<?>) map.get("depend")) {
          dependBuilder.add(dependency.toString());
        }
      } catch (ClassCastException ex) {
        throw new Exception("depend is of wrong type", ex);
      } catch (NullPointerException e) {
        throw new Exception("invalid dependency format", e);
      }
      depend = dependBuilder.build();
    }
  }

  private Map<?, ?> asMap(Object object) throws Exception {
    if (object instanceof Map) {
      return (Map<?, ?>) object;
    }
    throw new Exception(object + " is not properly structured.");
  }

}
