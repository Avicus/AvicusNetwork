package net.avicus.atlas.module.loadouts.type;

import javax.annotation.Nullable;
import lombok.ToString;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.compendium.number.PreparedNumberAction;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

@ToString(callSuper = true)
public class VisualLoadout extends Loadout {

  // Player weather
  private final WeatherType weather;

  // Player time
  private final PreparedNumberAction time;

  public VisualLoadout(boolean force, @Nullable Loadout parent, WeatherType weather,
      PreparedNumberAction time) {
    super(force, parent);
    this.weather = weather;
    this.time = time;
  }

  @Override
  public void give(Player player, boolean force) {
    // Weather
    if (this.weather != null) {
      player.setPlayerWeather(this.weather);
    }

    // Time
    if (this.time != null) {
      int current = ((Long) player.getPlayerTime()).intValue();
      player.setPlayerTime(this.time.perform(current), false);
    }

  }
}
