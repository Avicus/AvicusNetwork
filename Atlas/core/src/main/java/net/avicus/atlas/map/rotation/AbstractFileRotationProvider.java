package net.avicus.atlas.map.rotation;

import java.io.File;
import net.avicus.atlas.map.MapManager;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.util.Messages;

public abstract class AbstractFileRotationProvider extends AbstractRotationProvider {

  protected final File file;

  AbstractFileRotationProvider(File file, MapManager mm, final MatchFactory factory) {
    super(mm, factory);
    this.file = file;
  }

  @Override
  public final Rotation provideRotation() {
    if (!this.file.exists()) {
      this.error(Messages.ERROR_NO_ROTATION, this.file.getAbsolutePath());
      throw new IllegalStateException("No rotation provided.");
    } else if (!this.file.canRead()) {
      this.error(Messages.ERROR_CANNOT_READ, this.file.getAbsolutePath());
      throw new IllegalStateException("Cannot load file rotation.");
    }
    return this.createRotation();
  }

  protected abstract Rotation createRotation();
}
