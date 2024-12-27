package net.avicus.magma.channel.report;

import com.google.common.collect.ImmutableMap;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.avicus.compendium.commands.exception.TranslatableCommandErrorException;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.compendium.settings.Setting;
import net.avicus.compendium.settings.types.SettingTypes;
import net.avicus.magma.Magma;
import net.avicus.magma.MagmaConfig;
import net.avicus.magma.channel.staff.AbstractStaffChannel;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.rtp.RTPHelpers;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.text.Components;
import net.avicus.magma.util.MagmaTranslations;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.Duration;
import org.joda.time.Instant;

public class ReportChannel extends AbstractStaffChannel {

  static final Setting<Boolean> REPORT_NOTIFICATION_SETTING = new Setting<>(
      "report-notification",
      SettingTypes.BOOLEAN,
      true,
      MagmaTranslations.SETTING_REPORTNOTIFICATION_NAME.with(),
      MagmaTranslations.SETTING_REPORTNOTIFICATION_SUMMARY.with()
  );
  static final String ID = "report";
  private final Map<Player, AtomicInteger> counts = new WeakHashMap<>();
  private final Map<Player, Instant> lastReport = new WeakHashMap<>();

  ReportChannel() {
    super(ID, "channels.report", channelDescriptor("Report", ChatColor.RED));
  }

  private static ChatColor colorForReports(int reports) {
    if (reports >= 5) {
      return ChatColor.DARK_RED;
    }

    switch (reports) {
      case 1:
      case 2:
        return ChatColor.GREEN;
      case 3:
        return ChatColor.YELLOW;
      default:
        return ChatColor.RED;
    }
  }

  void report(Player source, Player target, String reason)
      throws TranslatableCommandErrorException {
    final Server localServer = Magma.get().localServer();
    final User sourceUser = Users.user(source);
    this.checkCooldown(source);
    this.dualSend(localServer, sourceUser, new BaseComponent[]{new TextComponent(reason)},
        ImmutableMap.of(
            "victim", target.getName(),
            "reports", String.valueOf(
                this.counts.computeIfAbsent(target, x -> new AtomicInteger()).incrementAndGet())
        ));

    final class ReportPublishTask extends BukkitRunnable {

      @Override
      public void run() {
        final User reported = Users.user(target);
        Magma.get().database().getReports().createReport(sourceUser, reported, localServer, reason);
      }
    }
    new ReportPublishTask().runTaskAsynchronously(Magma.get());
  }

  private void checkCooldown(Player source) throws TranslatableCommandErrorException {
    Instant instant = this.lastReport.get(source);
    Duration duration = MagmaConfig.Channel.Report.getCooldown()
        .minus(new Duration(instant, Instant.now()));
    if (instant != null && duration.isLongerThan(Duration.ZERO)) {
      throw new TranslatableCommandErrorException(
          (duration.getStandardSeconds() > 1 ? MagmaTranslations.REPORT_COOLDOWN_PLURAL
              : MagmaTranslations.REPORT_COOLDOWN_SINGULAR),
          new LocalizedNumber(duration.getStandardSeconds()));
    }

    this.lastReport.put(source, Instant.now());
  }

  @Override
  protected void preSend(final Player viewer) {
    if (PlayerSettings.get(viewer, REPORT_NOTIFICATION_SETTING)) {
      viewer.playSound(viewer.getLocation(), Sound.CAT_HISS, 1F, 2F);
    }
  }

  @Override
  @SuppressWarnings("OptionalGetWithoutIsPresent")
  protected void format(BaseComponent template, Server server, User source,
      BaseComponent[] components, Map<String, String> context) {
    final User victim = Magma.get().database().getUsers().findByName(context.get("victim")).get();
    final int reports = Integer.parseInt(context.get("reports"));

    template.addExtra(RTPHelpers.clickablePlayer(server, source, Locale.US));
    template.addExtra(Components.simple(" » ", ChatColor.GRAY));
    template.addExtra(RTPHelpers.clickablePlayer(server, victim, Locale.US));
    template.addExtra(Components.simple(": ", ChatColor.GRAY));
    template.addExtra(Components.simple(components, ChatColor.WHITE));
    template.addExtra(Components.simple(" [", ChatColor.GRAY));
    template.addExtra(Components.event(
        Components.simple(String.valueOf(reports), colorForReports(reports)),
        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports -p " + victim.getName()),
        new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
            MagmaTranslations.REPORT_RECENT_VIEW_PLAYER
                .with(ChatColor.WHITE, victim.getName()).render(null)
        })
    ));
    template.addExtra(Components.simple("]", ChatColor.GRAY));
  }
}
