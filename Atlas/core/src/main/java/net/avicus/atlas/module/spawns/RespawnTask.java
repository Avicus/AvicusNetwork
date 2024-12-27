package net.avicus.atlas.module.spawns;

import java.util.Locale;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.atlas.util.VersionUtil;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.LocalizedText;
import net.avicus.compendium.sound.SoundEvent;
import net.avicus.compendium.sound.SoundLocation;
import net.avicus.compendium.sound.SoundType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.github.paperspigot.Title;
import org.joda.time.Instant;
import org.spigotmc.event.entity.EntityDismountEvent;
import tc.oc.tracker.event.PlayerDamageEvent;

public class RespawnTask extends AtlasTask implements Listener {

  public static final String METADATA_TAG = "atlas.respawn-stand";

  private final SpawnsModule manager;
  private final Player player;
  private final Location location;
  @Getter
  private final long respawnTime;
  private final boolean freezePlayer;
  private final boolean blindPlayer;
  @Getter
  @Setter
  private boolean autoRespawn;
  private Optional<Entity> mount = Optional.empty();
  private BaseComponent title;

  private int currentTick;
  private boolean hasRefreshed;

  public RespawnTask(SpawnsModule manager, Player player, Instant respawnTime, boolean autoRespawn,
      boolean freezePlayer, boolean blindPlayer) {
    super();
    this.manager = manager;
    this.player = player;
    this.location = this.player.getLocation();
    this.respawnTime = respawnTime.getMillis();
    this.autoRespawn = autoRespawn;
    this.freezePlayer = freezePlayer;
    this.blindPlayer = blindPlayer;
    this.title = Messages.GENERIC_DEATH.with(ChatColor.RED).render(this.player);
    this.currentTick = 0;
    this.hasRefreshed = false;
  }

  @EventHandler
  public void onEntityDismount(EntityDismountEvent event) {
    if (!this.mount.isPresent()) {
      return;
    }

    if (event.getEntity().equals(this.player) && event.getDismounted().equals(this.mount.get())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPPlayerDamage(PlayerDamageEvent event) {
    if (event.getEntity().equals(this.player)) {
      event.setCancelled(true);
    }
  }

  public RespawnTask start() {
    this.repeat(0, 2);
    Events.register(this);

    if (!this.freezePlayer) {
      this.player.setAllowFlight(true);
      this.player.setFlying(true);
    }

    this.player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0), true);
    SoundEvent call = Events
        .call(new SoundEvent(this.player, SoundType.GOLEM_DEATH, SoundLocation.DEATH));
    call.getSound().play(this.player, 1F);
    this.player.setVelocity(new Vector());

    this.player.spigot().setCollidesWithEntities(false);
    // Todo?
    if (!VersionUtil.isCombatUpdate()) {
      this.player.spigot().setAffectsSpawning(false);
    }
    return this;
  }

  @Override
  public boolean cancel0() {
    Events.unregister(this);

    if (this.mount.isPresent()) {
      this.mount.get().remove();
    }
    if (this.player.isOnline()) {
      this.player.hideTitle();
    }
    return super.cancel0();
  }

  @Override
  public void run() {
    if (!this.player.isOnline()) {
      cancel0();
      return;
    }

    // TODO: move tick counting to AtlasTask somehow
    this.currentTick = currentTick + 2;

    // Need to mount them after death animation
    if (!this.mount.isPresent() && this.freezePlayer && currentTick >= 15) {
      ArmorStand entity = (ArmorStand) this.location.getWorld()
          .spawnEntity(this.location, EntityType.ARMOR_STAND);
      entity.setVisible(false); // can't see it
      entity.setHealth(0.5); // hides health bar
      entity.setMaxHealth(0.5);
      entity.setGravity(false);
      entity.setSmall(true);
      entity.setMarker(true);
      entity.setMetadata(METADATA_TAG, new FixedMetadataValue(Atlas.get(), true));

      this.mount = Optional.of(entity);
    }

    if (this.currentTick >= 15 && !this.hasRefreshed) {
      this.manager.getMatch().getRequiredModule(GroupsModule.class).refreshObservers();
      this.hasRefreshed = true;
    }

    if (this.blindPlayer) {
      this.player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1), true);
    }

    // Prevent player movement
    if (this.mount.isPresent()) {
      if (this.mount.get().getPassenger() == null) {
        this.mount.get().setPassenger(this.player);
      }
    }

    long now = System.currentTimeMillis();
    boolean isAfter = now >= this.respawnTime;

    // Match has since ended.
    if (!manager.getMatch().getRequiredModule(StatesModule.class).isPlaying()) {
      this.manager.stopRespawnTask(this.player);
      return;
    }

    if (this.autoRespawn && isAfter) {
      this.manager.stopRespawnTask(this.player);
      this.manager.spawn(this.player);
      return;
    }

    LocalizedText subtitle;
    if (isAfter) {
      subtitle = Messages.GENERIC_PUNCH_RESPAWN.with();
    } else {
      double seconds = ((double) this.respawnTime - (double) now) / 1000.0;
      LocalizedNumber secondsPart = new LocalizedNumber(seconds, 1, 1,
          TextStyle.ofColor(ChatColor.GREEN));
      if (this.autoRespawn) {
        subtitle = Messages.GENERIC_AUTO_RESPAWN.with(secondsPart);
      } else {
        subtitle = Messages.GENERIC_RESPAWN.with(secondsPart);
      }
    }
    subtitle.style().color(ChatColor.WHITE);

    this.player.sendTitle(new Title(this.title, subtitle.render(player), 0, 40, 20));
  }
}
