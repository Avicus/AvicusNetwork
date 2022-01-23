package net.avicus.atrio;

import lombok.Data;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.ParticleEffect;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

@Data
public class Present {

  private final Vector location;
  private final String slug;
  private final String family;

  public void spawnParticles() {
    ParticleEffect.FIREWORKS_SPARK
        .display(0f, 0f, 0f, 0.2f, 8,
            getCenter(location.toLocation(AtrioPlugin.getInstance().getWorld())),
            20);
  }

  public void find(Player player) {
    HookTask.of(() -> {
      User user = Users.user(player);
      Pair<Boolean, String> res = Magma.get().getApiClient().getPresents()
          .find(user, this.getFamily(), this.getSlug());
      if (res.getKey()) {
        player.sendMessage(ChatColor.GREEN + res.getValue());
        Bukkit
            .broadcastMessage(player.getDisplayName() + ChatColor.GOLD + " just found a present!");
        HookTask.of(() -> {
          Firework f = (Firework) player.getWorld()
              .spawnEntity(player.getLocation(), EntityType.FIREWORK);
          FireworkMeta meta = f.getFireworkMeta();
          meta.setPower(2);
          meta.addEffect(FireworkEffect.builder()
              .trail(true)
              .withColor(Color.RED)
              .withColor(Color.GREEN)
              .with(Type.BALL)
              .build());
          f.setFireworkMeta(meta);
        }).now();
      } else {
        player.sendMessage(ChatColor.RED + res.getValue());
      }
    }).nowAsync();
  }


  public Location getCenter(Location loc) {
    return new Location(loc.getWorld(),
        getRelativeCoord(loc.getBlockX()),
        getRelativeCoord(loc.getBlockY()),
        getRelativeCoord(loc.getBlockZ()));
  }

  private double getRelativeCoord(int i) {
    double d = i;
    d = d < 0 ? d - .5 : d + .5;
    return d;
  }
}