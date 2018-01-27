package net.avicus.atlas.module.loadouts.type;

import javax.annotation.Nullable;
import lombok.ToString;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.module.loadouts.Loadout;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

@ToString(callSuper = true)
public class VehicleLoadout extends Loadout {

  public static final String STICKY_TAG = "loadout.vehicle.sticky";
  public static final String REMOVE_TAG = "loadout.vehicle.remove";

  private final EntityType vehicle;
  private final Vector velocity;
  private final boolean sticky;
  private final boolean removeOnExit;

  public VehicleLoadout(boolean force, @Nullable Loadout parent, EntityType vehicle,
      Vector velocity, boolean sticky, boolean removeOnExit) {
    super(force, parent);
    this.vehicle = vehicle;
    this.velocity = velocity;
    this.sticky = sticky;
    this.removeOnExit = removeOnExit;
  }

  @Override
  public void give(Player player, boolean force) {
    Entity spawned = player.getWorld().spawnEntity(player.getLocation(), vehicle);
    spawned.setPassenger(player);
    spawned.setVelocity(this.velocity);
    spawned.setMetadata(STICKY_TAG, new FixedMetadataValue(Atlas.get(), sticky));
    spawned.setMetadata(REMOVE_TAG, new FixedMetadataValue(Atlas.get(), removeOnExit));
  }
}
