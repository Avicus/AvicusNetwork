package net.avicus.atlas.fun;

import java.util.Random;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.util.AtlasTask;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.github.paperspigot.Title;

public class Friday13 implements Listener {

  private static final Random RANDOM = new Random();
  private final Atlas atlas;

  public Friday13(Atlas atlas) {
    this.atlas = atlas;
    startTask();
  }

  private void startTask() {
    AtlasTask.of(() -> {
      if (RANDOM.nextBoolean()) {
        return;
      }

      TextComponent message = new TextComponent("HAPPY FRIDAY THE 13TH!");
      message.setColor(ChatColor.GOLD);

      Bukkit.getOnlinePlayers().forEach(p -> {
        if (RANDOM.nextBoolean()) {
          return;
        }

        if (RANDOM.nextBoolean()) {
          p.setVelocity(new Vector(0, 0.3 + RANDOM.nextDouble(), 0));
        }

        if (RANDOM.nextBoolean()) {
          p.playSound(p.getLocation(), Sound.AMBIENCE_CAVE, .5f, 1f);
        }

        p.sendTitle(new Title(message, new TextComponent(), 2, 5, 2));

        if (RANDOM.nextBoolean()) {
          p.getWorld().strikeLightningEffect(p.getLocation());
        }

        AtlasTask.of(() -> {
          if (RANDOM.nextBoolean()) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 40, 1));
          } else if (RANDOM.nextBoolean()) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1));
          } else {
            p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10, 1));
          }
        }).now();
      });
    }).repeatAsync(0, 90 * 20);

    AtlasTask.of(() -> {
      if (RANDOM.nextBoolean()) {
        return;
      }

      World match = this.atlas.getMatchManager().getRotation().getMatch().getWorld();

      long time = match.getTime();
      match.setTime(18000);

      Bukkit.getOnlinePlayers().forEach(p -> {
        if (RANDOM.nextBoolean()) {
          p.getWorld().strikeLightningEffect(p.getLocation());
        }
      });
      match.setTime(time);
    }).repeatAsync(0, 120 * 20);
  }


}
