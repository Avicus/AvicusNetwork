package net.avicus.hook.gadgets.types.morph;

import javax.annotation.Nullable;
import lombok.Data;
import lombok.Getter;
import me.libraryaddict.disguise.disguisetypes.RabbitType;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.entity.EntityType;

public enum MorphEntity {

  COW(EntityType.COW, null),
  BAT(EntityType.BAT, null),
  WOLF(EntityType.WOLF, null),
  PIG(EntityType.PIG, null),
  SHEEP(EntityType.SHEEP, null),
  VILLAGER(EntityType.VILLAGER, null),
  BLAZE(EntityType.BLAZE, null),
  SLIME(EntityType.SLIME, null),
  ENDERMAN(EntityType.ENDERMAN, null),
  SPIDER(EntityType.SPIDER, null),
  SILVERFISH(EntityType.SILVERFISH, null),
  OCELOT(EntityType.OCELOT, null),
  IRON_GOLEM(EntityType.IRON_GOLEM, null),
  CHICKEN(EntityType.CHICKEN, null),
  TNT(EntityType.PRIMED_TNT, null),
  BOAT(EntityType.BOAT, null),
  MINECART(EntityType.MINECART, null),
  CREEPER(EntityType.CREEPER, null),
  SKELETON(EntityType.SKELETON, null),
  GUARDIAN(EntityType.GUARDIAN, null),
  SQUID(EntityType.SQUID, null),
  RABBIT(EntityType.RABBIT, new RabbitMorphData(RabbitType.BROWN)),
  WHITE_RABBIT(EntityType.RABBIT, new RabbitMorphData(RabbitType.WHITE)),
  SNOW_GOLEM(EntityType.SNOWMAN, null),
  MAGMA_CUBE(EntityType.MAGMA_CUBE, null),

  SANTA(EntityType.PLAYER, new PlayerMorphData("Santa"));

  @Getter
  private final EntityType entity;
  @Getter
  private final
  @Nullable
  MorphData data;

  MorphEntity(EntityType entity,
      @Nullable MorphData data) {
    this.entity = entity;
    this.data = data;
  }

  public String prettyName() {
    return WordUtils.capitalize(name().replaceAll("_", " ").toLowerCase());
  }

  @Override
  public String toString() {
    return this.entity.name();
  }

  public interface MorphData {

  }

  @Data
  public static class PlayerMorphData implements MorphData {

    private final String playerName;
  }

  @Data
  public static class RabbitMorphData implements MorphData {

    private final RabbitType type;
  }
}