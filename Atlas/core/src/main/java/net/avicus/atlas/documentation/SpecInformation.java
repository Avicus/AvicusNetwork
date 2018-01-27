package net.avicus.atlas.documentation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.avicus.magma.util.Version;

@Builder
public class SpecInformation {

  @Getter
  private final Version added;
  @Getter
  private final Version deprecated;
  @Getter
  private final Version removed;
  @Getter
  private final Map<Version, ArrayList<String>> changes;
  @Getter
  private final Map<Version, ArrayList<String>> breakingChanges;
  @Setter
  @Getter
  private String name;

  public static class SpecInformationBuilder {

    public SpecInformationBuilder change(Version v, String s) {
      if (this.changes == null) {
        this.changes = Maps.newHashMap();
      }

      if (!this.changes.containsKey(v)) {
        this.changes.put(v, Lists.newArrayList());
      }

      this.changes.get(v).add(s);

      return this;
    }

    public SpecInformationBuilder breakingChange(Version v, String s) {
      if (this.breakingChanges == null) {
        this.breakingChanges = Maps.newHashMap();
      }

      if (!this.breakingChanges.containsKey(v)) {
        this.breakingChanges.put(v, Lists.newArrayList());
      }

      this.breakingChanges.get(v).add(s);

      return this;
    }
  }
}
