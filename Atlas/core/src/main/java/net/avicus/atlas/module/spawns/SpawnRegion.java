package net.avicus.atlas.module.spawns;

import java.util.Optional;
import java.util.Random;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.runtimeconfig.RuntimeConfigurable;
import net.avicus.atlas.runtimeconfig.fields.AngleProviderField;
import net.avicus.atlas.runtimeconfig.fields.ConfigurableField;
import net.avicus.atlas.runtimeconfig.fields.OptionalField;
import net.avicus.atlas.runtimeconfig.fields.RegisteredObjectField;
import net.avicus.compendium.points.AngleProvider;
import net.avicus.magma.util.region.BoundedRegion;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

@ToString
public class SpawnRegion implements RuntimeConfigurable {

  @Getter
  private BoundedRegion region;
  @Getter
  private Optional<AngleProvider> yaw;
  @Getter
  private Optional<AngleProvider> pitch;

  public SpawnRegion(BoundedRegion region, Optional<AngleProvider> yaw,
      Optional<AngleProvider> pitch) {
    this.region = region;
    this.yaw = yaw;
    this.pitch = pitch;
  }

  public Vector randomPosition(Random random) {
    return this.region.getRandomPosition(random);
  }

  public Vector getCenter() {
    return this.region.getCenter();
  }

  @Override
  public String getDescription(CommandSender viewer) {
    return "Spawn Region";
  }

  @Override
  public ConfigurableField[] getFields() {
    return new ConfigurableField[]{
        new RegisteredObjectField<>("Region", () -> this.region, (v) -> this.region = v, BoundedRegion.class),
        new OptionalField<>("Yaw", () -> this.yaw, (v) -> this.yaw = v, new AngleProviderField("yaw")),
        new OptionalField<>("Pitch", () -> this.pitch, (v) -> this.pitch = v, new AngleProviderField("yaw"))
    };
  }
}
