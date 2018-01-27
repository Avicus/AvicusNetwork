package net.avicus.magma.network.server;

import com.google.common.collect.ArrayListMultimap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.ServerGroup;
import net.avicus.magma.redis.Redis;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Retrieves statuses from Redis, and updates this server's
 * status to Redis.
 */
public class StatusUpdateTask extends BukkitRunnable {

  private final Random RANDOM = new Random();

  public void start() {
    this.runTaskTimerAsynchronously(Magma.get(), 0, 20 * 3);
  }

  @Override
  public void run() {
    {
      Redis redis = Magma.get().getRedis();

      // Statuses
      ServerStatus status = Servers.generateStatus();
      Servers.syncUp(redis, status);
      Servers.syncDown(redis);
    }

    {
      // Servers
      List<Server> servers = Magma.get().database().getServers().select().execute();

      // Groups
      List<ServerGroup> serverGroups = Magma.get().database().getServerGroups().select().execute();
      ArrayListMultimap<ServerGroup, Integer> members = ArrayListMultimap.create();
      Map<Integer, ItemStack> icons = new HashMap<>();
      for (ServerGroup group : serverGroups) {
        members.putAll(group, group.serverIds(Magma.get().database()));
        icons.put(group.getId(), parseIcon(group.getIcon()));
      }

      // Update Cache
      Servers.updateServerCache(servers, members, icons);
    }
  }

  private ItemStack parseIcon(String s) {
    if (s == null || s.isEmpty()) {
      return new ItemStack(Material.BARRIER);
    }
    String[] split = s.split(":");
    Material material = Material
        .valueOf(split[0].toUpperCase().replaceAll(" ", "_").replaceAll("-", "_"));
    ItemStack stack = new ItemStack(material);
    if (split.length > 1) {
      short damage = Short.valueOf(split[1]);
      stack.setDurability(damage);

      if (split.length > 2) {
        // Special
        String special = split[2];
        switch (special.toLowerCase()) {
          case "g": // glow
            ItemMeta meta = stack.getItemMeta();
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            stack.setItemMeta(meta);
            break;
          case "r": // random damage
            int max = Math.max(16, material.getMaxDurability());
            stack.setDurability((short) RANDOM.nextInt(max));
            break;
          default:
            throw new RuntimeException("Unknown special type: " + special);
        }
      }
    }

    return stack;
  }
}
