package net.avicus.atrio;

import static net.avicus.hook.gadgets.backpack.BackpackMenu.createBackpackOpener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import lombok.Data;
import lombok.Getter;
import net.avicus.compendium.boss.BossBar;
import net.avicus.compendium.boss.BossBarOverlay;
import net.avicus.compendium.plugin.CompendiumPlugin;
import net.avicus.hook.Hook;
import net.avicus.hook.credits.Credits;
import net.avicus.hook.gadgets.types.device.DeviceGadget;
import net.avicus.hook.utils.HookTask;
import net.avicus.magma.NetworkIdentification;
import net.avicus.magma.database.model.impl.Announcement;
import net.avicus.magma.network.server.Servers;
import net.avicus.magma.util.region.BoundedRegion;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Button;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.material.Door;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.TrapDoor;
import org.bukkit.util.Vector;
import org.github.paperspigot.Title;
import tc.oc.tracker.event.PlayerCoarseMoveEvent;

public class AtrioListener implements Listener {

  private static List<Sound> JOIN_SOUNDS = Arrays.asList(Sound.BLAZE_DEATH,
      Sound.CAT_MEOW,
      Sound.CHICKEN_HURT,
      Sound.LEVEL_UP,
      Sound.EXPLODE,
      Sound.FIREWORK_LARGE_BLAST2,
      Sound.FIREWORK_LAUNCH,
      Sound.FIREWORK_TWINKLE,
      Sound.ENDERMAN_TELEPORT,
      Sound.VILLAGER_HAGGLE,
      Sound.ZOMBIE_INFECT,
      Sound.ZOMBIE_REMEDY,
      Sound.WITHER_SPAWN,
      Sound.ORB_PICKUP
  );
  private final AtrioPlugin plugin;
  private final Spawn spawn;
  private final List<ServerSign> signs;
  private final List<Portal> portals;
  private final List<Pad> pads;
  private final List<Present> presents;
  private final Optional<ItemStack> book;
  private final Map<Player, AtrioSidebar> sidebars;
  private final Map<Player, BossBar> bossBars;
  private final List<Announcement> headerOptions;
  @Getter
  private final List<Announcement> titleOptions;
  private final Random random;

  public AtrioListener(AtrioPlugin plugin, Spawn spawn, List<ServerSign> signs,
      List<Portal> portals, List<Pad> pads, List<Present> presents, Optional<ItemStack> book) {
    this.plugin = plugin;
    this.spawn = spawn;
    this.signs = signs;
    this.portals = portals;
    this.pads = pads;
    this.presents = presents;
    this.book = book;
    this.sidebars = new HashMap<>();
    this.bossBars = new HashMap<>();
    this.headerOptions = new ArrayList<>();
    this.titleOptions = Hook.database().getAnnouncements().findByType(Announcement.Type.POPUP);
    this.random = new Random();

    List<Announcement> found = Hook.database().getAnnouncements()
        .findByType(Announcement.Type.MOTD);
    if (!found.isEmpty()) {
      headerOptions.addAll(found);
    } else {
      headerOptions.add(new Announcement(NetworkIdentification.URL));
    }

    HookTask.of(() -> {
      this.presents.forEach(Present::spawnParticles);
    }).repeat(50, 25);
  }

  public void resetPlayer(Player player) {
    player.teleport(this.spawn.getRegion().getRandomPosition(this.random)
        .toLocation(AtrioPlugin.getInstance().getWorld(), this.spawn.getYaw(),
            this.spawn.getPitch()));
    player.setVelocity(this.spawn.getVelocity());
    player.setGameMode(GameMode.SURVIVAL);
    player.setAllowFlight(false);
    player.setHealth(20);
    player.setFoodLevel(20);
    player.getInventory().clear();

    PlayerInventory inventory = player.getInventory();

    if (AtrioConfig.Inventory.ServerMenu.isEnabled()) {
      inventory
          .setItem(AtrioConfig.Inventory.ServerMenu.getSlot(),
              Servers.createMenuOpener(player));
    }

    if (AtrioConfig.Inventory.Backpack.isEnabled()) {
      inventory.setItem(AtrioConfig.Inventory.Backpack.getSlot(), createBackpackOpener(player));
    }

    if (AtrioConfig.Inventory.Store.isEnabled()) {
      inventory
          .setItem(AtrioConfig.Inventory.Store.getSlot(),
              Credits.createGadgetStoreOpener(player));
    }

    if (this.book.isPresent()) {
      inventory.setItem(AtrioConfig.Inventory.Book.getSlot(), this.book.get());
    }

    MaterialData helmet = toMaterial(
        AtrioConfig.Inventory.Armor.getHelmet().replace(' ', '_').toUpperCase());
    MaterialData chestplate = toMaterial(
        AtrioConfig.Inventory.Armor.getChestplate().replace(' ', '_').toUpperCase());
    MaterialData leggings = toMaterial(
        AtrioConfig.Inventory.Armor.getLeggings().replace(' ', '_').toUpperCase());
    MaterialData boots = toMaterial(
        AtrioConfig.Inventory.Armor.getBoots().replace(' ', '_').toUpperCase());

    inventory.setHelmet(helmet.toItemStack(1));
    inventory.setChestplate(chestplate.toItemStack(1));
    inventory.setLeggings(leggings.toItemStack(1));
    inventory.setBoots(boots.toItemStack(1));
  }

  private MaterialData toMaterial(String ident) {
    String[] fixed = ident.replace(' ', '_').toUpperCase().split(":");
    Material mat = Material.valueOf(fixed[0]);
    if (fixed.length > 1) {
      return new MaterialData(mat, Byte.valueOf(fixed[1]));
    }
    return new MaterialData(mat);
  }

  private void setupBar(Player player) {
    BossBar bar = CompendiumPlugin.getInstance().getBossBarManager().create(player);
    bar.setCreateFog(false);
    bar.setDarkenSky(false);
    TextComponent text = new TextComponent(NetworkIdentification.URL);
    text.setColor(ChatColor.AQUA);
    bar.setName(text);
    bar.setOverlay(BossBarOverlay.PROGRESS);
    bar.setPercent(1.0f);
    bar.setVisible(true);
    this.bossBars.put(player, bar);
  }

  private void sendTitle(Player player) {
    if (this.titleOptions.isEmpty()) {
      return;
    }

    TextComponent network = new TextComponent(NetworkIdentification.NAME);
    network.setColor(ChatColor.GREEN);

    TextComponent selected = new TextComponent(
        TextComponent.fromLegacyText(net.md_5.bungee.api.ChatColor
            .translateAlternateColorCodes('&',
                this.titleOptions
                    .get(Math.max(this.random.nextInt(this.titleOptions.size()) - 1, 0))
                    .getBody().trim())));
    Title title = new Title(network, selected, 40, 200, 80);

    player.sendTitle(title);
  }

  private void playSound(Player player) {
    Sound chosen = JOIN_SOUNDS.get(Math.max(0, this.random.nextInt(JOIN_SOUNDS.size() - 1)));
    float pitch = 0.5f + this.random.nextFloat();
    player.playSound(player.getLocation(), chosen, 1.0f, pitch);
  }

  private void populateHeaderFooter(Player player) {
    TextComponent network = new TextComponent("The Avicus Network");
    network.setColor(ChatColor.GREEN);

    BaseComponent[] headArray = new BaseComponent[]{
        network,
        new TextComponent(TextComponent.fromLegacyText("\n" + net.md_5.bungee.api.ChatColor
            .translateAlternateColorCodes('&', "&c" + this.headerOptions
                .get(Math.max(this.random.nextInt(this.headerOptions.size()) - 1, 0)).getBody()
                .trim())))
    };

    TextComponent header = new TextComponent(headArray);

    TextComponent footer = new TextComponent(Hook.server().getName());
    footer.setColor(ChatColor.GOLD);

    player.setPlayerListHeaderFooter(header, footer);
  }

  @EventHandler(ignoreCancelled = true)
  public void onJoin(PlayerJoinEvent event) {
    event.setJoinMessage(null);
    resetPlayer(event.getPlayer());

    AtrioSidebar sidebar = new AtrioSidebar(this.plugin, event.getPlayer());
    sidebar.start();
    this.sidebars.put(event.getPlayer(), sidebar);

    setupBar(event.getPlayer());
    populateHeaderFooter(event.getPlayer());
    sendTitle(event.getPlayer());
    playSound(event.getPlayer());
  }

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    resetPlayer(event.getPlayer());
  }

  @EventHandler
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
      event.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    AtrioSidebar sidebar = this.sidebars.remove(event.getPlayer());
    if (sidebar != null) {
      sidebar.stop();
    }

    BossBar bar = this.bossBars.remove(event.getPlayer());
    if (bar != null) {
      bar.destroy();
    }

    event.setQuitMessage(null);
  }

  @EventHandler
  public void onPlayerKick(PlayerKickEvent event) {
    event.setLeaveMessage(null);
  }

  @EventHandler
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getEntity();

    if (player.getGameMode() == GameMode.SURVIVAL) {
      event.setCancelled(true);
      player.setSaturation(100);
      player.setFoodLevel(20);
    }
  }

  @EventHandler
  public void onEntityDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getEntity();

    if (player.getGameMode() != GameMode.SURVIVAL) {
      return;
    }

    event.setCancelled(true);

    if (event.getCause() == DamageCause.VOID) {
      resetPlayer(player);
    }
  }

  @EventHandler
  public void onExplode(EntityExplodeEvent event) {
    event.setCancelled(true);
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.getAction() != Action.LEFT_CLICK_BLOCK
        && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      Material holding = event.getMaterial();
      if (holding == Material.ENCHANTED_BOOK || holding == Material.BOOK
          || holding == Material.WRITTEN_BOOK || holding == Material.BOOK_AND_QUILL) {
        return;
      }

      Class<? extends MaterialData> data = event.getClickedBlock().getType().getData();
      if (data == Door.class || data == TrapDoor.class || data == DirectionalContainer.class
          || data == Lever.class || data == Button.class) {
        return;
      }
    }

    for (ServerSign sign : this.signs) {
      if (sign.isSign(event.getClickedBlock())) {
        sign.connect(event.getPlayer());
        event.setCancelled(true);
        return;
      }
    }
    for (Portal portal : this.portals) {
      if (portal.getType() == Portal.PortalType.CLICK && portal.getEnter()
          .contains(event.getClickedBlock())) {
        portal.teleport(event.getPlayer());
        event.setCancelled(true);
        return;
      }
    }

    for (Present present : this.presents) {
      if (present.getLocation().equals(event.getClickedBlock().getLocation().toVector())) {
        present.find(event.getPlayer());
        event.setCancelled(true);
        return;
      }
    }

    event.setCancelled(true);
  }

  @EventHandler
  public void onItemSpawn(ItemSpawnEvent event) {
    if (!DeviceGadget.ALLOW_DROP.get(event.getEntity().getItemStack().getItemMeta())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onMove(PlayerCoarseMoveEvent event) {
    if (event.getTo().getBlockY() <= 0) {
      resetPlayer(event.getPlayer());
    }

    for (Portal portal : this.portals) {
      if (portal.getType() == Portal.PortalType.ENTER && portal.getEnter()
          .contains(event.getTo())) {
        portal.teleport(event.getPlayer());
        return;
      }
    }
    for (Pad pad : this.pads) {
      if (pad.getPad().contains(event.getTo())) {
        pad.bounce(event.getPlayer());
        return;
      }
    }
  }

  @EventHandler
  public void onHangingBreak(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof ItemFrame) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onWeatherChange(WeatherChangeEvent event) {
    if (event.toWeatherState()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onServerListPing(ServerListPingEvent event) {
    event.setMotd(Hook.server().getName());
  }

  @EventHandler
  public void onMobSpawn(CreatureSpawnEvent event) {
    CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
    if (reason == CreatureSpawnEvent.SpawnReason.CHUNK_GEN ||
        reason == CreatureSpawnEvent.SpawnReason.DEFAULT ||
        reason == CreatureSpawnEvent.SpawnReason.NATURAL ||
        reason == CreatureSpawnEvent.SpawnReason.NETHER_PORTAL ||
        reason == CreatureSpawnEvent.SpawnReason.REINFORCEMENTS) {
      event.setCancelled(true);
    }
  }

  @Data
  public static class Spawn {

    private final BoundedRegion region;
    private final float yaw;
    private final float pitch;
    private final Vector velocity;
  }
}