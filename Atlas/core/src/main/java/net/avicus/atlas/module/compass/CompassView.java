package net.avicus.atlas.module.compass;

import lombok.Getter;
import lombok.ToString;
import net.avicus.compendium.locale.text.Localizable;
import org.bukkit.Location;

@ToString
public class CompassView {

  @Getter
  private final Location target;
  @Getter
  private final Localizable display;

  public CompassView(Location target, Localizable display) {
    this.target = target;
    this.display = display;
  }
}
