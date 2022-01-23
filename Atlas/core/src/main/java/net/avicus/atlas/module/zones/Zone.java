package net.avicus.atlas.module.zones;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.runtimeconfig.RuntimeConfigurable;
import net.avicus.atlas.runtimeconfig.fields.ConfigurableField;
import net.avicus.atlas.runtimeconfig.fields.RegisteredObjectField;
import net.avicus.magma.util.region.Region;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@Getter
@ToString(exclude = "match")
public abstract class Zone implements Listener, RuntimeConfigurable {

  protected final Match match;
  protected Region region;
  protected final Optional<ZoneMessage> message;

  public Zone(Match match, Region region, Optional<ZoneMessage> message) {
    this.match = match;
    this.region = region;
    this.message = message;
  }

  public boolean isObserving(Match match, Player player) {
    try {
      return match.getRequiredModule(GroupsModule.class).isObservingOrDead(player);
    } catch (RuntimeException e) {
      // Not in a group, count as observing.
      return true;
    }
  }

  public abstract boolean isActive();

  public void message(Player player) {
    this.message.ifPresent(m -> m.send(player));
  }

  @Override
  public String getDescription(CommandSender viewer) {
    boolean in = viewer instanceof Player && this.region.contains(((Player) viewer).getLocation());
    if (in) {
      return ChatColor.GREEN + " In Now";
    }
    return ChatColor.RED + " Not In";
  }

  @Override
  public List<RuntimeConfigurable> getChildren() {
    return this.message.<List<RuntimeConfigurable>>map(Collections::singletonList)
        .orElseGet(() -> RuntimeConfigurable.super.getChildren());
  }

  @Override
  public ConfigurableField[] getFields() {
    return new ConfigurableField[]{new RegisteredObjectField<>("Region", () -> this.region, (v) -> this.region = v, Region.class)};
  }
}
