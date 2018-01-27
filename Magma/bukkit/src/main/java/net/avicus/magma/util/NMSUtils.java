package net.avicus.magma.util;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntityTrackerEntry;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.joda.time.Duration;

public class NMSUtils {

  // These entity type IDs are hard-coded in a huge conditional statement in EntityTrackerEntry.
  // There is no nice way to get at them.
  private static final Map<Class<? extends org.bukkit.entity.Entity>, Integer> ENTITY_TYPE_IDS = ImmutableMap
      .of(
          org.bukkit.entity.Item.class, 2
      );
  private static Random random = new Random();

  public static void sendPacket(Player bukkitPlayer, Object packet) {
    if (bukkitPlayer.isOnline()) {
      EntityPlayer nmsPlayer = ((CraftPlayer) bukkitPlayer).getHandle();
      nmsPlayer.playerConnection.sendPacket((Packet) packet);
    }
  }

  private static EntityTrackerEntry getTrackerEntry(Entity nms) {
    return ((WorldServer) nms.getWorld()).getTracker().trackedEntities.get(nms.getId());
  }

  private static EntityTrackerEntry getTrackerEntry(org.bukkit.entity.Entity entity) {
    return getTrackerEntry(((CraftEntity) entity).getHandle());
  }

  private static void sendPacketToViewers(Entity entity, Object packet) {
    EntityTrackerEntry entry = getTrackerEntry(entity);
    for (EntityPlayer viewer : entry.trackedPlayers) {
      viewer.playerConnection.sendPacket((Packet) packet);
    }
  }

  private static double randomEntityVelocity() {
    return random.nextDouble() - 0.5d;
  }

  public static void showFakeItems(Plugin plugin, Player viewer, Location location,
      org.bukkit.inventory.ItemStack item, int count, Duration duration) {
    if (count <= 0) {
      return;
    }

    final EntityPlayer nmsPlayer = ((CraftPlayer) viewer).getHandle();
    final int[] entityIds = new int[count];

    for (int i = 0; i < count; i++) {
      final EntityItem entity = new EntityItem(nmsPlayer.getWorld(), location.getX(),
          location.getY(), location.getZ(), CraftItemStack.asNMSCopy(item));

      entity.motX = randomEntityVelocity();
      entity.motY = randomEntityVelocity();
      entity.motZ = randomEntityVelocity();

      sendPacket(viewer,
          new PacketPlayOutSpawnEntity(entity, ENTITY_TYPE_IDS.get(org.bukkit.entity.Item.class)));
      sendPacket(viewer,
          new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true));

      entityIds[i] = entity.getId();
    }

    scheduleEntityDestroy(plugin, viewer.getUniqueId(), duration, entityIds);
  }

  private static void scheduleEntityDestroy(Plugin plugin, UUID viewerUuid, Duration delay,
      int[] entityIds) {
    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
      final Player viewer = plugin.getServer().getPlayer(viewerUuid);
      if (viewer != null) {
        sendPacket(viewer, new PacketPlayOutEntityDestroy(entityIds));
      }
    }, delay.getStandardSeconds() * 20);
  }

  public static NBTTagCompound getNBT(org.bukkit.entity.Entity entity) {
    Entity nmsEntity = ((CraftEntity) entity).getHandle();

    NBTTagCompound tag = new NBTTagCompound();

    //writes entity's nbt data to OUR tag object
    nmsEntity.c(tag);
    return tag;
  }

  public static void setNBT(org.bukkit.entity.Entity entity, NBTTagCompound compound) {
    Entity nmsEntity = ((CraftEntity) entity).getHandle();

    ((EntityLiving) nmsEntity).a(compound);
  }

  public static void playDeathAnimation(Player player) {
    EntityPlayer handle = ((CraftPlayer) player).getHandle();
    PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(handle.getId(),
        handle.getDataWatcher(), false);

    boolean replaced = false;

    // 3 - marks as a float field (see DataWatcher.class for a mapping).
    // 6 - the field number for the health data.
    // 0f - Makes the client think the entity is dead so it will render the death animation.
    DataWatcher.WatchableObject zeroHealth = new DataWatcher.WatchableObject(3, 6, 0f);

    // Can't use a PacketPlayOutEntityStatus because it doesn't actually work.
    // packet.b is the list of watchable objects in the packet.
    // Have to do this loop to weed out any health data already in the packet.
    // item.a() is the field number for the watcher.

    if (packet.b != null) {
      for (int i = 0; i < packet.b.size(); i++) {
        DataWatcher.WatchableObject item = packet.b.get(i);
        if (6 == item.a()) {
          packet.b.set(i, zeroHealth);
          replaced = true;
        }
      }
    }

    if (!replaced) {
      if (packet.b == null) {
        packet.b = Collections.singletonList(zeroHealth);
      } else {
        packet.b.add(zeroHealth);
      }
    }

    sendPacketToViewers(handle, packet);
  }
}
