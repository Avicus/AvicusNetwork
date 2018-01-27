package net.avicus.atlas.module.stats.action.lifetime.type;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.stats.action.base.PlayerAction;

@ToString
public class PlayerLifetime extends ActionLifetime<PlayerAction> {

  @Getter
  private final UUID player;

  public PlayerLifetime(Instant start, UUID player) {
    super(start);
    this.player = player;
  }
}
