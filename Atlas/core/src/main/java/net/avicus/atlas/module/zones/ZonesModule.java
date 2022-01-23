package net.avicus.atlas.module.zones;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.runtimeconfig.RuntimeConfigurable;
import net.avicus.atlas.util.Events;
import org.bukkit.command.CommandSender;

@ToString
public class ZonesModule implements Module, RuntimeConfigurable {

  private final List<Zone> zones;

  public ZonesModule(Match match, List<Zone> zones) {
    this.zones = zones;
  }

  @Override
  public void open() {
    this.zones.stream().filter(Zone::isActive).forEach(Events::register);
    this.zones.stream()
        .filter(z -> z instanceof ZoneNode && z.isActive())
        .flatMap(z -> ((ZoneNode) z).getZones().stream()).forEach(Events::register);
  }

  @SuppressWarnings("unchecked")
  public <T extends Zone> List<T> getZones(Class<T> type) {
    List<T> res = new ArrayList<T>();
    for (Zone zone : this.zones) {
      Class test = zone.getClass();
      if (type.isAssignableFrom(test)) {
        res.add((T) zone);
      }
    }
    return res;
  }

  @Override
  public void close() {
    Events.unregister(this.zones);
    this.zones.stream()
        .filter(z -> z instanceof ZoneNode && z.isActive())
        .flatMap(z -> ((ZoneNode) z).getZones().stream()).forEach(Events::unregister);
  }

  @Override
  public List<RuntimeConfigurable> getChildren() {
    return this.zones.stream().map(z -> (RuntimeConfigurable)z).collect(Collectors.toList());
  }

  @Override
  public String getDescription(CommandSender viewer) {
    return "Zones";
  }
}
