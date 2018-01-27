package net.avicus.atlas.module.loadouts.type;

import javax.annotation.Nullable;
import lombok.ToString;
import net.avicus.atlas.module.loadouts.Loadout;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@ToString(callSuper = true)
public class GameModeLoadout extends Loadout {

  private final GameMode gamemode;

  public GameModeLoadout(boolean force, @Nullable Loadout parent, GameMode gamemode) {
    super(force, parent);
    this.gamemode = gamemode;
  }

  @Override
  public void give(Player player, boolean force) {
    player.setGameMode(this.gamemode);
  }
}
