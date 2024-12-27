package net.avicus.atlas.sets.arcade.carts;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Getter;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.sets.arcade.ArcadeGame;
import net.avicus.atlas.sets.arcade.util.SideBarUtils;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.magma.util.Sidebar;
import net.avicus.magma.util.region.shapes.CircleRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDespawnInVoidEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

public class CartsModule extends ArcadeGame {

  private static final FireworkEffect EFFECT = FireworkEffect.builder()
      .withColor(Color.ORANGE)
      .trail(true)
      .flicker(true)
      .with(Type.BALL_LARGE)
      .build();

  @Getter
  private final Vector center;
  private final int radius;
  private final int radiusSubtract;
  private final int height;
  private final List<Entity> carts = Lists.newArrayList();
  private final List<Entity> emptyCarts = Lists.newArrayList();
  private final Collection<Player> inCarts = Lists.newArrayList();
  private final AtomicInteger broadcasts = new AtomicInteger();
  private final AtomicInteger round = new AtomicInteger();
  private AtlasTask checkTask;
  private AtlasTask alertTask;
  private CircleRegion region;

  public CartsModule(Match match, Vector center, int radius, int radiusSubtract, int height) {
    super(match, (p, g) -> g.getId().equals("ffa"));
    this.center = center;
    this.radius = radius;
    this.radiusSubtract = radiusSubtract;
    this.height = height;
    this.region = new CircleRegion(center, radius);
    setTasks();
  }

  private static void log(String message) {
    Bukkit.getLogger().info("[CartsModule] " + message);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onEnter(VehicleEnterEvent event) {
    if (event.getVehicle().getType() == EntityType.MINECART) {
      this.inCarts.add((Player) event.getEntered());
      this.emptyCarts.remove(event.getVehicle());
      log(event.getEntered().getName() + " entered a cart.");
      updateSideBar();
    }
  }

  @EventHandler
  public void onDespawn(EntityDespawnInVoidEvent event) {
    if (event.getEntity().getType() == EntityType.MINECART) {
      this.emptyCarts.remove(event.getEntity());
    }
  }

  @EventHandler
  public void onCartDamage(VehicleDamageEvent event) {
    if (event.getVehicle().getLocation().getY() > 1) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onCollide(VehicleEntityCollisionEvent e) {
    if (e.getVehicle().getType() == EntityType.MINECART) {
      e.setCollisionCancelled(true);
      e.setPickupCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onDamage(EntityDamageEvent event) {
    if (event.getEntity().getLocation().getY() > 0) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onExit(VehicleExitEvent event) {
    if (event.getVehicle().getType() == EntityType.MINECART) {
      event.setCancelled(true);
    }
  }

  @Override
  public void matchStart() {
    AtlasTask.of(() -> this::spawnCarts).later(15);
  }

  @Override
  public void matchEnd() {
    this.checkTask.cancel0();
    this.alertTask.cancel0();
  }

  private void setTasks() {
    this.checkTask = AtlasTask.of(() -> {
      if (!anyAlive()) {
        return;
      }

      if (full() || this.broadcasts.get() > 5) {
        reset();
      }
    });
    this.alertTask = AtlasTask.of(() -> {
      if (!full()) {
        Bukkit.broadcast(Messages.UI_CARTS_PICK.with(ChatColor.GOLD));
        this.broadcasts.addAndGet(1);
        this.emptyCarts.forEach(c -> {
          Firework firework = (Firework) getMatch().getWorld()
              .spawnEntity(c.getLocation(), EntityType.FIREWORK);
          FireworkMeta meta = firework.getFireworkMeta();
          meta.setPower(2);
          meta.addEffect(EFFECT);
          firework.setFireworkMeta(meta);
        });
        playSound(Sound.BLAZE_HIT, 1.4f);
        if (this.broadcasts.get() == 5) {
          Bukkit.broadcast(Messages.UI_CARTS_FINAL.with(ChatColor.RED));
          playSound(Sound.WITHER_SPAWN, 1.2f);
        }
      }
    });
  }

  private boolean full() {
    return this.emptyCarts.isEmpty();
  }

  private List<Player> notInCarts() {
    return getPlayingPlayers().stream().filter(p -> !this.inCarts.contains(p))
        .collect(Collectors.toList());
  }

  private void shrink() {
    this.region = new CircleRegion(this.center,
        this.region.getRadius() - this.radiusSubtract);
    log("New radius " + this.region.getRadius());
  }

  private void reset() {
    log("Running reset...");
    this.checkTask.cancel0();
    this.alertTask.cancel0();

    // Have to recreate the tasks because re-running throws an error.
    setTasks();

    this.broadcasts.set(0);
    playSound(Sound.CREEPER_HISS, .8f);
    List<Player> notInCarts = notInCarts();
    eliminateIf(notInCarts::contains, (p) -> {
      log("Eliminating " + p.getName());
      p.sendMessage(Messages.UI_CARTS_ELIMINATED.with(ChatColor.RED));
    });

    shrink();

    this.carts.forEach(c -> {
      c.eject();
      c.remove();
    });

    reSpawn();

    this.carts.clear();
    this.emptyCarts.clear();
    this.inCarts.clear();

    AtlasTask.of(() -> this::spawnCarts).later(5 * 20);
  }

  @Override
  public List<String> getRows(Player player, GroupsModule groups, Sidebar sidebar,
      ObjectivesModule module) {
    List<String> res = Lists.newArrayList();
    res.add(Messages.UI_ROUND.with(ChatColor.AQUA, new LocalizedNumber(this.round.intValue(),
        TextStyle.ofColor(ChatColor.GOLD).bold())).render(player).toLegacyText());
    res.add("");
    getPlayingPlayers().forEach(p ->
        res.add(SideBarUtils.booleanPlayer(p, inCarts.contains(p)))
    );
    return res;
  }

  private void spawnCarts() {
    if (!getMatch().getRequiredModule(StatesModule.class).isPlaying()) {
      return;
    }

    this.round.incrementAndGet();
    updateSideBar();
    int comp = getGroupsModule().getCompetitors().size();
    log(comp + " players remain");

    int carts = Math.max(1, comp - 1);
    for (int i = 0; i < carts; i++) {
      Vector where = this.region.getRandomPosition(RANDOM);
      where.setY(this.height);
      Entity spawned = getMatch().getWorld()
          .spawnEntity(where.toLocation(getMatch().getWorld()), EntityType.MINECART);
      this.emptyCarts.add(spawned);
      this.carts.add(spawned);
    }
    Bukkit.broadcast(Messages.UI_CARTS_SPAWNED.with(ChatColor.GREEN));
    playSound(Sound.NOTE_PIANO, 1.4f);
    log("Spawned " + this.carts.size() + " carts");
    this.checkTask.repeat(4, 10);
    this.alertTask.repeat(120, 10 * 20);
  }
}
