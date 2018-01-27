package net.avicus.hook.gadgets;

import java.util.ArrayList;
import java.util.Arrays;
import net.avicus.compendium.WeightedRandomizer;
import net.avicus.compendium.commands.AvicusCommandsRegistration;
import net.avicus.compendium.sound.SoundLocation;
import net.avicus.compendium.sound.SoundType;
import net.avicus.hook.Hook;
import net.avicus.hook.gadgets.backpack.BackpackCommand;
import net.avicus.hook.gadgets.types.arrowtrails.ArrowTrailManager;
import net.avicus.hook.gadgets.types.badge.BadgeGadget;
import net.avicus.hook.gadgets.types.badge.BadgeManager;
import net.avicus.hook.gadgets.types.badge.BadgeSymbol;
import net.avicus.hook.gadgets.types.device.DeviceManager;
import net.avicus.hook.gadgets.types.device.entity.EntityGun;
import net.avicus.hook.gadgets.types.device.entity.GunType;
import net.avicus.hook.gadgets.types.item.ItemManager;
import net.avicus.hook.gadgets.types.map.AtlasGadgetManager;
import net.avicus.hook.gadgets.types.morph.MorphEntity;
import net.avicus.hook.gadgets.types.morph.MorphGadget;
import net.avicus.hook.gadgets.types.morph.MorphManager;
import net.avicus.hook.gadgets.types.sound.SoundGadget;
import net.avicus.hook.gadgets.types.sound.SoundManager;
import net.avicus.hook.gadgets.types.statreset.StatResetManager;
import net.avicus.hook.gadgets.types.track.TrackGadget;
import net.avicus.hook.gadgets.types.track.TrackManager;
import net.avicus.hook.gadgets.types.track.TrackType;
import net.avicus.hook.gadgets.types.trail.TrailGadget;
import net.avicus.hook.gadgets.types.trail.TrailManager;
import net.avicus.hook.gadgets.types.trail.TrailType;
import net.avicus.magma.Magma;
import net.avicus.magma.module.gadgets.Gadget;
import net.avicus.magma.module.gadgets.GadgetManager;
import net.avicus.magma.module.gadgets.Gadgets;
import net.avicus.magma.module.gadgets.crates.CrateManager;
import net.avicus.magma.module.gadgets.crates.KeyManager;
import net.avicus.magma.module.gadgets.crates.TypeManager;
import org.bukkit.ChatColor;

public class HookGadgets {

  public static void init(AvicusCommandsRegistration cmds) {
    if (!Magma.get().getMm().hasModule(Gadgets.class)) {
      return;
    }

    ArrayList<GadgetManager> managers = new ArrayList<>();

    managers.addAll(Arrays.asList(
        BadgeManager.INSTANCE,
        CrateManager.INSTANCE,
        KeyManager.INSTANCE,
        SoundManager.INSTANCE,
        ItemManager.INSTANCE,
        StatResetManager.INSTANCE,
        TrackManager.INSTANCE,
        TrailManager.INSTANCE,
        DeviceManager.INSTANCE,
        AtlasGadgetManager.INSTANCE
    ));

    if (Hook.disguises()) {
      managers.add(MorphManager.INSTANCE);
    }

    managers.add(ArrowTrailManager.INSTANCE);

    Gadgets gadgetsModule = Magma.get().getMm().get(Gadgets.class);

    managers.forEach(
        gadgetsModule::registerManager);

    cmds.register(BackpackCommand.class);

    loadCrateTypes();
  }

  private static void loadCrateTypes() {
    TypeManager.getTypeOptional("alpha").ifPresent(t -> {
      WeightedRandomizer.<Gadget>builder()
          // Todo: Chroma
          // Sound
          .item(new SoundGadget(SoundLocation.CREDIT_GAIN, SoundType.BURP), 4)
          .item(new SoundGadget(SoundLocation.CREDIT_GAIN, SoundType.SNARE), 4)
          .item(new SoundGadget(SoundLocation.CREDIT_GAIN, SoundType.ANVIL), 4)
          // Track
          .item(new TrackGadget(TrackType.THIRTEEN, 100), 4)
          .item(new TrackGadget(TrackType.CAT, 100), 3)
          // Trail
          .item(new TrailGadget(TrailType.VOID), 3)
          .item(new TrailGadget(TrailType.PORTAL), 3)
          .item(new TrailGadget(TrailType.RUNES), 2)
          // Morph
          .item(new MorphGadget(MorphEntity.COW), 1)
          .item(new MorphGadget(MorphEntity.SHEEP), 1)
          .item(new MorphGadget(MorphEntity.PIG), 1)
          // Badge
          .item(new BadgeGadget(BadgeSymbol.ISOTOXAL, ChatColor.BLUE), 1)
          // Gun
          .item(new EntityGun(false, 25, GunType.EGG), 4)
          .item(new EntityGun(false, 25, GunType.CHICKEN), 2)
          .apply(t.getRandomizer());
    });

    TypeManager.getTypeOptional("beta").ifPresent(t -> WeightedRandomizer.<Gadget>builder()
        // Todo: Teleport Wand
        // Sound
        .item(new SoundGadget(SoundLocation.CREDIT_GAIN, SoundType.BASS), 5)
        .item(new SoundGadget(SoundLocation.CREDIT_GAIN, SoundType.PIANO), 4)
        // Track
        .item(new TrackGadget(TrackType.CHIRP, 100), 1)
        .item(new TrackGadget(TrackType.FAR, 100), 3)
        // Trail
        .item(new TrailGadget(TrailType.CLOUD), 3)
        .item(new TrailGadget(TrailType.NOTE), 1)
        // Morph
        .item(new MorphGadget(MorphEntity.OCELOT), 1)
        .item(new MorphGadget(MorphEntity.SPIDER), 1)
        .item(new MorphGadget(MorphEntity.SILVERFISH), 1)
        // Badge
        .item(new BadgeGadget(BadgeSymbol.STAR_BADGE, ChatColor.GOLD), 4)
        // Track
        .item(new EntityGun(false, 10, GunType.BAT), 3)
        .item(new EntityGun(false, 10, GunType.OCELOT), 2)
        .item(new EntityGun(false, 10, GunType.SKELETON), 1)
        .apply(t.getRandomizer())
    );

    TypeManager.getTypeOptional("gamma").ifPresent(t -> WeightedRandomizer.<Gadget>builder()
        // Todo: Lightning Staff
        // Sound
        .item(new SoundGadget(SoundLocation.CREDIT_GAIN, SoundType.PLING), 3)
        .item(new SoundGadget(SoundLocation.CREDIT_GAIN, SoundType.PLOP), 4)
        .item(new SoundGadget(SoundLocation.CREDIT_GAIN, SoundType.ENDERDRAGON), 4)
        // Trail
        .item(new TrailGadget(TrailType.SPELL), 2)
        .item(new TrailGadget(TrailType.FIRE), 1)
        // Morph
        .item(new MorphGadget(MorphEntity.BLAZE), 1)
        .item(new MorphGadget(MorphEntity.ENDERMAN), 1)
        .item(new MorphGadget(MorphEntity.IRON_GOLEM), 1)
        // Badge
        .item(new BadgeGadget(BadgeSymbol.PROPELLER, ChatColor.RED), 2)
        // Gun
        .item(new EntityGun(false, 5, GunType.TNT), 1)
        .item(new EntityGun(false, 8, GunType.GOLEM), 1)
        .apply(t.getRandomizer())
    );

    TypeManager.getTypeOptional("vote").ifPresent(t -> WeightedRandomizer.<Gadget>builder()
        .item(new MorphGadget(MorphEntity.BLAZE), 24)
        .item(new MorphGadget(MorphEntity.ENDERMAN), 24)
        .item(new MorphGadget(MorphEntity.IRON_GOLEM), 19)
        .item(new EntityGun(false, 55, GunType.TNT), 12)
        .item(new EntityGun(false, 44, GunType.GOLEM), 16)
        .apply(t.getRandomizer())
    );
  }
}
