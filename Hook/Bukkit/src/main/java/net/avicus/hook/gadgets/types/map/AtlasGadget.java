package net.avicus.hook.gadgets.types.map;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.avicus.atlas.match.Match;
import net.avicus.magma.module.gadgets.AbstractGadget;
import net.avicus.magma.module.gadgets.EmptyGadgetContext;
import org.bukkit.entity.Player;

@Getter
public abstract class AtlasGadget extends AbstractGadget<EmptyGadgetContext<AtlasGadget>> {

  public AtlasGadget() {
    super(AtlasGadgetManager.INSTANCE);
  }

  @Override
  public EmptyGadgetContext<AtlasGadget> defaultContext() {
    return new EmptyGadgetContext<AtlasGadget>(this);
  }

  @Override
  public EmptyGadgetContext<AtlasGadget> deserializeContext(JsonObject json) {
    return new EmptyGadgetContext<AtlasGadget>(this);
  }

  public abstract boolean onUse(Player player, Match match,
      EmptyGadgetContext<AtlasGadget> context);

  @Override
  public boolean isAllowedInMatches() {
    return true;
  }
}
