package net.avicus.atrio;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import net.avicus.compendium.sound.SoundEvent;
import net.avicus.compendium.sound.SoundLocation;
import net.avicus.compendium.sound.SoundType;
import net.avicus.hook.HookConfig;
import net.avicus.hook.utils.Events;
import net.avicus.hook.utils.HookTask;
import net.avicus.magma.NetworkIdentification;
import net.avicus.magma.database.model.impl.Announcement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class AnnouncementsTask extends HookTask {

  private static final Random random = new Random();

  @Getter
  private final List<Announcement> tips;

  private Iterator<Announcement> announcementIterator;

  public AnnouncementsTask(List<Announcement> tips) {
    this.tips = tips;
  }

  public void start() {
    if (!HookConfig.Announcements.isEnabled()) {
      return;
    }
    repeatAsync(0, 20 * HookConfig.Announcements.getDelay());
    this.announcementIterator = tips.iterator();
  }

  @Override
  public void run() throws Exception {
    if (this.tips.isEmpty()) {
      return;
    }

    if (!this.announcementIterator.hasNext()) {
      Collections.shuffle(this.tips);
      this.announcementIterator = this.tips.iterator();
    }

    Announcement announcement = this.announcementIterator.next();

    List<ChatColor> colors = Arrays
        .asList(ChatColor.AQUA, ChatColor.GREEN, ChatColor.BLUE, ChatColor.LIGHT_PURPLE,
            ChatColor.YELLOW, ChatColor.RED);
    ChatColor color = colors.get(random.nextInt(colors.size()));

    String[] prefixes = new String[]{"Tip", "Info", NetworkIdentification.NAME, "News", "§kwoo"};
    String prefix = prefixes[random.nextInt(prefixes.length)];

    // Todo: Localize?
    String colored = ChatColor.translateAlternateColorCodes('&', announcement.getBody());
    Bukkit.broadcastMessage(
        ChatColor.BLUE + "[" + ChatColor.AQUA + ChatColor.BOLD + prefix + ChatColor.BLUE + "] "
            + color + colored);
    Bukkit.getOnlinePlayers().forEach((p) -> {
      SoundEvent call = Events.call(new SoundEvent(p, SoundType.CLICK, SoundLocation.TIP_MESSAGE));
      call.getSound().play(p, 1F);
    });
  }
}
