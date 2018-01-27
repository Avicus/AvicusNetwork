package net.avicus.hook.gadgets.types.device.entity;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

@Getter
public enum GunType {
  TNT(EntityType.PRIMED_TNT, "TNT", Material.TNT),
  GOLEM(EntityType.IRON_GOLEM, "Iron Golem", Material.IRON_INGOT),
  OCELOT(EntityType.OCELOT, "Ocelot", Material.MONSTER_EGG),
  SQUID(EntityType.SQUID, "Squid", Material.INK_SACK),
  BAT(EntityType.BAT, "Bat", Material.MONSTER_EGG),
  SKELETON(EntityType.SKELETON, "Skeleton", Material.BONE),
  CHICKEN(EntityType.CHICKEN, "Chicken", Material.EGG),
  BOAT(EntityType.BOAT, "Boat", Material.BOAT),
  EGG(EntityType.EGG, "Egg", Material.EGG),
  SLIME(EntityType.SLIME, "Slime", Material.SLIME_BALL),
  WITCH(EntityType.WITCH, "Witch", Material.POTION),
  ENDERMITE(EntityType.ENDERMITE, "Ender Mite", Material.ENDER_PEARL),
  ENDERMAN(EntityType.ENDERMAN, "Enderman", Material.ENDER_PEARL),
  CREEPER(EntityType.CREEPER, "Creeper", Material.SULPHUR),
  SILVERFISH(EntityType.SILVERFISH, "Silver Fish", Material.STONE);

  private final EntityType type;
  private final String human;
  private final Material icon;

  GunType(EntityType type, String human, Material icon) {
    this.type = type;
    this.human = human;
    this.icon = icon;
  }
}
