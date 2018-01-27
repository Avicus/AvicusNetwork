package net.avicus.atlas.match;

import java.io.IOException;
import lombok.Getter;
import net.avicus.atlas.AtlasConfig;
import net.avicus.atlas.map.rotation.Rotation;

public class MatchManager {

  @Getter
  private final MatchFactory factory;
  @Getter
  private final Rotation rotation;

  public MatchManager(MatchFactory factory, Rotation rotation) {
    this.factory = factory;
    this.rotation = rotation;
  }

  public void start() throws IOException {
    this.rotation.start();
  }

  public void shutdown() {
    if (AtlasConfig.isDeleteMatches()) {
      for (final Match match : this.rotation.getMatches()) {
        try {
          match.unloadWorld();
          match.deleteWorld();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
