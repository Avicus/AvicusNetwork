package net.avicus.magma.module.freeze;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.Magma;
import net.avicus.magma.MagmaConfig;
import net.avicus.magma.channel.staff.StaffChannels;
import net.avicus.magma.module.CommandModule;
import net.avicus.magma.module.ListenerModule;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

public class FreezeModule implements CommandModule, ListenerModule {

  private final Map<UUID, FreezeStand> stands = new HashMap<>();

  @Override
  public void registerCommands(CommandsManagerRegistration registrar) {
    registrar.register(Commands.class);
  }

  public boolean isFrozen(final Entity entity) {
    return this.stands.containsKey(entity.getUniqueId());
  }

  public void setFrozen(final Player victim, final boolean frozen, final boolean extinguish) {
    if (frozen) {
      this.stands.computeIfAbsent(victim.getUniqueId(), FreezeStand::new).create();
      if (extinguish) {
        this.extinguish(victim);
      }
    } else {
      final FreezeStand stand = this.stands.remove(victim.getUniqueId());
      if (stand != null) {
        stand.remove();
      }
    }
  }

  private void extinguish(final Player victim) {
    final int tntRadius = MagmaConfig.Freeze.Radius.getTnt();
    if (tntRadius != -1) {
      victim.getNearbyEntities(tntRadius, tntRadius, tntRadius)
          .stream()
          .filter(entity -> entity instanceof TNTPrimed)
          .forEach(Entity::remove);
    }

    final int extinguishRadius = MagmaConfig.Freeze.Radius.getExtinguish();
    if (extinguishRadius != -1) {
      final Vector vector = new Vector(extinguishRadius, extinguishRadius, extinguishRadius);
      final Vector min = victim.getLocation().clone().subtract(vector).toVector();
      final Vector max = victim.getLocation().clone().add(vector).toVector();
      for (int x = min.getBlockX(); x < max.getBlockX(); x++) {
        for (int y = min.getBlockY(); y < max.getBlockY(); y++) {
          for (int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
            final Block block = victim.getWorld().getBlockAt(x, y, z);
            if (block.getType() == Material.FIRE) {
              block.setType(Material.AIR);
            }
          }
        }
      }

      for (final Entity entity : victim
          .getNearbyEntities(extinguishRadius, extinguishRadius, extinguishRadius)) {
        entity.setFireTicks(0);
      }
    }
  }

  private void freezeCommand(CommandSender source, Player victim) {
    this.setFrozen(victim, !this.isFrozen(victim), true);
    final boolean frozen = this.isFrozen(victim);
    StaffChannels.STAFF_CHANNEL.simpleLocalSend(source,
        (frozen ? MagmaTranslations.FREEZE_BROADCAST_FROZE
            : MagmaTranslations.FREEZE_BROADCAST_UNFROZE)
            .with(ChatColor.AQUA, source.getName(), victim.getName()).render(null));
    victim.sendMessage(
        (frozen ? MagmaTranslations.FREEZE_FROZEN : MagmaTranslations.FREEZE_UNFROZEN)
            .with(ChatColor.AQUA, Users.getLocalizedDisplay(Users.user(source))));
  }


  @EventHandler(priority = EventPriority.HIGHEST)
  public void entityDamage(final EntityDamageEvent event) {
    if ((event instanceof EntityDamageByEntityEvent && this
        .isFrozen(((EntityDamageByEntityEvent) event).getDamager())) || this
        .isFrozen(event.getEntity())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void entityDismount(final EntityDismountEvent event) {
    final Entity entity = event.getEntity();
    if (this.isFrozen(entity)) {
      new BukkitRunnable() {
        @Override
        public void run() {
          event.getDismounted().setPassenger(entity);
        }
      }.runTask(Magma.get());
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void playerInteract(final PlayerInteractEvent event) {
    if (this.isFrozen(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void playerJoin(final PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    if (this.isFrozen(player)) {
      this.setFrozen(player, true, false);
      StaffChannels.STAFF_CHANNEL.simpleLocalSend(null,
          MagmaTranslations.FREEZE_BROADCAST_JOINED.with(ChatColor.AQUA, player.getName())
              .render(null));
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void playerPickupItem(final PlayerPickupItemEvent event) {
    if (this.isFrozen(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void playerQuit(final PlayerQuitEvent event) {
    final Player player = event.getPlayer();
    if (this.isFrozen(player)) {
      final FreezeStand stand = this.stands.get(player.getUniqueId());
      if (stand != null) {
        stand.remove();
      }
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void playerDropItem(final PlayerDropItemEvent event) {
    if (this.isFrozen(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void inventoryClick(final InventoryClickEvent event) {
    if (this.isFrozen(event.getWhoClicked())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void vehicleEnter(final VehicleEnterEvent event) {
    if (this.isFrozen(event.getEntered())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void vehicleExit(final VehicleExitEvent event) {
    if (this.isFrozen(event.getExited())) {
      event.setCancelled(true);
    }
  }

  public static class Commands {

    @Command(aliases = {"freeze",
        "f"}, desc = "Freeze a player.", usage = "<player>", min = 1, max = 1)
    @CommandPermissions("hook.freeze")
    public static void freeze(CommandContext context, CommandSender sender) {
      @Nullable Player target = Bukkit.getPlayer(context.getString(0));
      if (target == null) {
        sender.sendMessage(MagmaTranslations.ERROR_UNKNOWN_PLAYER
            .with(ChatColor.RED, new UnlocalizedText(context.getString(0))));
        return;
      }

      Magma.get().getMm().get(FreezeModule.class).freezeCommand(sender, target);
    }
  }
}
