package net.avicus.atlas.module.zones;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.runtimeconfig.RuntimeConfigurable;
import net.avicus.magma.util.region.Region;
import org.bukkit.command.CommandSender;

@Getter
@ToString
public class ZoneNode extends Zone {

  private final List<Zone> zones;

  public ZoneNode(Match match, Region region, Optional<ZoneMessage> message, List<Zone> zones) {
    super(match, region, message);
    this.zones = zones;
  }

  @Override
  public boolean isActive() {
    return this.zones.stream().anyMatch(Zone::isActive);
  }

  @Override
  public String getDescription(CommandSender viewer) {
    return "Zone Group";
  }

  @Override
  public List<RuntimeConfigurable> getChildren() {
    List<RuntimeConfigurable> res = this.zones.stream().map(z -> (RuntimeConfigurable)z).collect(Collectors.toList());
    res.addAll(super.getChildren());
    return res;
  }
}
