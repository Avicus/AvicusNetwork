package net.avicus.magma.network.rtp;

import com.google.common.collect.Maps;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import java.util.Map;
import java.util.UUID;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import net.avicus.compendium.commands.exception.TranslatableCommandErrorException;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.Magma;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.Session;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.CommandModule;
import net.avicus.magma.module.ListenerModule;
import net.avicus.magma.network.server.Servers;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.util.MagmaTranslations;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class RemoteTeleports implements CommandModule, ListenerModule {

  public static final String PERMISSION = "hook.rtp.command";
  public static ParticipationDelegate participationDelegate = viewer -> false;
  private final Map<UUID, UUID> queue = Maps.newHashMap();

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  private static void teleport(final Player victim, final UUID targetId) {
    if (victim == null || targetId == null) {
      return;
    }

    final Player target = victim.getServer().getPlayer(targetId);
    if (target == null) {
      victim.sendMessage(MagmaTranslations.GUI_GENERIC_ONLINE_NOT
          .with(Users.getDisplay(Magma.get().database().getUsers().findByUuid(targetId).get())));
      return;
    }

    if (participationDelegate.isParticipating(victim)) {
      return;
    }

    victim.teleport(target);
    victim.sendMessage(MagmaTranslations.RTP_GENERIC_SUCCESS_PLAYER
        .with(ChatColor.GRAY, Users.getLocalizedDisplay(Users.user(target))));
  }

  @Command(aliases = {"rtp", "goto",
      "go"}, desc = "Teleport to a player.", min = 1, max = 1, usage = "<player>")
  public static void rtp(CommandContext ctx, CommandSender source) throws CommandException {
    MustBePlayerCommandException.ensurePlayer(source);
    if (!source.hasPermission(PERMISSION)) {
      throw new CommandPermissionsException();
    }

    if (participationDelegate.isParticipating((Player) source)) {
      throw new TranslatableCommandErrorException(
          MagmaTranslations.RTP_PLAYER_TELEPORT_FAIL_PARTICIPATING);
    }

    new BukkitRunnable() {
      @Override
      public void run() {
        final String name = ctx.getString(0);
        final Player target = Bukkit.getPlayerExact(name);
        if (target != null) {
          teleport((Player) source, target.getUniqueId());
          return;
        }

        final Database db = Magma.get().database();
        final User user = db.getUsers().findByName(name).orElse(null);
        if (user == null) {
          source
              .sendMessage(MagmaTranslations.ERROR_UNKNOWN_PLAYER.with(new UnlocalizedText(name)));
          return;
        }

        final Session session = db.getSessions().findLatest(user.getId()).orElse(null);
        if (session == null || !session.isActive()) {
          source.sendMessage(
              MagmaTranslations.GUI_GENERIC_ONLINE_NOT.with(Users.getLocalizedDisplay(user)));
          return;
        }

        final Server server = session.getServer(db);
        source.sendMessage(MagmaTranslations.RTP_PLAYER_TELEPORTING_REMOTE
            .with(ChatColor.GREEN, Users.getLocalizedDisplay(user),
                new UnlocalizedText(server.getName(), ChatColor.GOLD)));
        Magma.get().getRedis().publish(new RemoteTeleportRedisMessage(Magma.get().localServer(),
            ((Player) source).getUniqueId(), user.getUniqueId()));
        Servers.connect((Player) source, server, false, false);
      }
    }.runTaskAsynchronously(Magma.get());
  }

  public static void clickable(BaseComponent component, User user) {
    component
        .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rtp " + user.getName()));
    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
        new TextComponent("Click to teleport to " + Users.getDisplay(user))
    }));
  }

  @Override
  public void enable() {
    Magma.get().getRedis().register(new RemoteTeleportRedisMessageConsumer(this));
  }

  @Override
  public void registerCommands(CommandsManagerRegistration registrar) {
    registrar.register(RemoteTeleports.class);
  }

  void queue(Server server, UUID victim, UUID target) {
    if (server.getId() != Magma.get().localServer().getId()) {
      this.queue.put(victim, target);
      this.delayedTeleport(Bukkit.getPlayer(victim));
      new BukkitRunnable() {
        @Override
        public void run() {
          RemoteTeleports.this.queue.remove(victim);
        }
      }.runTaskLater(Magma.get(), 80);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void playerJoin(final PlayerJoinEvent event) {
    this.delayedTeleport(event.getPlayer());
  }

  private void delayedTeleport(final Player victim) {
    new BukkitRunnable() {
      @Override
      public void run() {
        if (victim == null || !victim.isOnline()) {
          return;
        }

        final UUID targetId = RemoteTeleports.this.queue.remove(victim.getUniqueId());
        teleport(victim, targetId);
      }
    }.runTaskLater(Magma.get(), 40);
  }

  public interface ParticipationDelegate {

    boolean isParticipating(Player viewer);
  }
}
