package net.avicus.atrio;

import java.util.List;
import java.util.Random;
import net.avicus.compendium.utils.Task;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

public class FireworkSpawnTask extends Task {

  private final List<Color[]> colors;
  private final Color[] fade;
  private final FireworkEffect.Type type;

  public FireworkSpawnTask(List<Color[]> colors, Color[] fade, FireworkEffect.Type type) {
    this.colors = colors;
    this.fade = fade;
    this.type = type;
  }

  @Override
  public Plugin getPlugin() {
    return AtrioPlugin.getInstance();
  }

  @Override
  public void run() throws Exception {
    Random random = new Random();

    if (Bukkit.getOnlinePlayers().isEmpty() || random.nextBoolean()) {
      return;
    }

    Player randomPlayer = (Player) Bukkit.getOnlinePlayers().toArray()[random
        .nextInt(Bukkit.getOnlinePlayers().size())];

    int toSpawn = random.nextInt(3) + 1;

    for (int i = 0; i < toSpawn; i++) {
      Location randomLocation = randomPlayer.getLocation().clone()
          .add(random.nextInt(10) - 5, random.nextInt(4) - 2, random.nextInt(10) - 5);

      Firework firework = (Firework) AtrioPlugin.getInstance().getWorld()
          .spawnEntity(randomLocation, EntityType.FIREWORK);
      FireworkMeta meta = firework.getFireworkMeta();
      FireworkEffect.Builder effect = FireworkEffect.builder().flicker(false);

      effect.withColor(colors.get(random.nextInt(colors.size() - 1)));

      if (this.fade.length > 0) {
        effect.withFade(this.fade);
      }

      effect.with(this.type);
      effect.trail(true);

      meta.addEffect(effect.build());
      meta.setPower(random.nextInt(2) + 1);
      firework.setFireworkMeta(meta);
    }
  }
}
