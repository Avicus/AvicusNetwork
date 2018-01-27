package net.avicus.magma.module.gadgets;

import com.google.common.collect.ArrayListMultimap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.avicus.compendium.WeightedRandomizer;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.BackpackGadget;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.event.user.AsyncHookLoginEvent;
import net.avicus.magma.event.user.AsyncHookLogoutEvent;
import net.avicus.magma.module.ListenerModule;
import net.avicus.magma.module.Module;
import net.avicus.magma.module.gadgets.crates.CrateGadget;
import net.avicus.magma.module.gadgets.crates.CrateManager;
import net.avicus.magma.module.gadgets.crates.KeyGadget;
import net.avicus.magma.module.gadgets.crates.TypeManager;
import net.avicus.magma.module.gadgets.ranks.RankGadget;
import net.avicus.magma.module.gadgets.ranks.RankManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.joda.time.DateTime;
import org.joda.time.Duration;

public class Gadgets implements Module, ListenerModule {

  private static ArrayList<GadgetManager> managers = new ArrayList<>();
  private static ArrayListMultimap<UUID, GadgetContext> gadgets;
  private static Map<GadgetContext, Integer> backpackGadgets;

  @Override
  public void enable() {
    gadgets = ArrayListMultimap.create();
    backpackGadgets = new HashMap<>();

    registerManager(CrateManager.INSTANCE);

    registerManager(RankManager.INSTANCE);

    TypeManager.registerType("alpha", WeightedRandomizer.<Gadget>builder().build());
    TypeManager.registerType("beta", WeightedRandomizer.<Gadget>builder().build());
    TypeManager.registerType("gamma", WeightedRandomizer.<Gadget>builder().build());
    TypeManager.registerType("vote", WeightedRandomizer.<Gadget>builder()
        // Keys
        .item(new KeyGadget(TypeManager.getType("alpha")), 14)
        .item(new KeyGadget(TypeManager.getType("beta")), 7)
        .item(new KeyGadget(TypeManager.getType("gamma")), 5)
        // Crates
        .item(new CrateGadget(TypeManager.getType("alpha")), 20)
        .item(new CrateGadget(TypeManager.getType("beta")), 14)
        .item(new CrateGadget(TypeManager.getType("gamma")), 5)
        // Ranks - hardcoded names :(
        .item(new RankGadget(Magma.get().database().getRanks().getOrCreate("Gold"), Optional.of(
            DateTime.now().plus(Duration.standardDays(7)).toDate())), 1)
        .item(new RankGadget(Magma.get().database().getRanks().getOrCreate("Emerald"), Optional.of(
            DateTime.now().plus(Duration.standardDays(5)).toDate())), 0.6)
        .item(new RankGadget(Magma.get().database().getRanks().getOrCreate("Diamond"), Optional.of(
            DateTime.now().plus(Duration.standardDays(1)).toDate())), 0.3)
        .build());
  }

  public void registerManager(GadgetManager manager) {
    managers.add(manager);
    manager.init();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onAsyncHookLogin(AsyncHookLoginEvent event) {
    loadAll(event.getUser());
  }

  @EventHandler
  public void onAsyncHookLogout(AsyncHookLogoutEvent event) {
    unloadAll(event.getUser());
  }

  public Optional<GadgetManager> getManager(String type) {
    for (GadgetManager manager : managers) {
      if (manager.getType().equalsIgnoreCase(type)) {
        return Optional.of(manager);
      }
    }
    return Optional.empty();
  }

  public List<GadgetContext> getGadgets(UUID user) {
    return gadgets.get(user);
  }

  public void loadAll(User user) {
    Magma.get().database().getBackpackGadgets().findByUser(user.getId())
        .stream()
        .forEach((backpackGadget) -> {
          GadgetManager manager = getManager(backpackGadget.getGadgetType()).orElse(null);

          if (manager == null) {
            return;
          }

          GadgetContext context = manager
              .deserializeContext(backpackGadget.getGadget(), backpackGadget.getContext());
          load(user, backpackGadget, context);
        });
  }

  @SuppressWarnings("unchecked")
  public void load(User user, BackpackGadget backpackGadget, GadgetContext context) {
    gadgets.put(user.getUniqueId(), context);
    backpackGadgets.put(context, backpackGadget.getId());
    context.getManager().onAsyncLoad(user, context);
  }

  public void unloadAll(User user) {
    List<GadgetContext> clone = new ArrayList<>(gadgets.get(user.getUniqueId()));
    for (GadgetContext context : clone) {
      unload(user, context);
    }
  }

  @SuppressWarnings("unchecked")
  public void unload(User user, GadgetContext context) {
    context.getManager().onAsyncUnload(user, context);
    gadgets.remove(user.getUniqueId(), context);
    backpackGadgets.remove(context);
  }

  @SuppressWarnings("unchecked")
  public <G extends Gadget<C>, C extends GadgetContext<G>> void use(Player player, C gadget) {
    GadgetManager<G, C> manager = gadget.getGadget().getManager();
    manager.onUse(player, gadget);
  }

  private Optional<Integer> backpackGadgetId(GadgetContext context) {
    return Optional.ofNullable(backpackGadgets.get(context));
  }

  /**
   * Create a new backpack gadget (to database).
   */
  public BackpackGadget createBackpackGadget(User user, GadgetContext context, boolean load,
      Date date) {
    BackpackGadget entry = new BackpackGadget(
        user.getId(),
        context.getManager().getType(),
        context.getGadget().serialize(),
        context.serialize(),
        date);
    Magma.get().database().getBackpackGadgets().insert(entry).execute();

    // Load into server
    if (load) {
      load(user, entry, context);
    }

    return entry;
  }

  /**
   * Delete a backpack gadget.
   */
  public void deleteBackpackGadget(GadgetContext context) {
    Integer backpackGadgetId = backpackGadgetId(context).orElse(null);

    if (backpackGadgetId == null) {
      return;
    }

    gadgets.values().remove(context);
    backpackGadgets.remove(context);
    Magma.get().database().getBackpackGadgets().delete(backpackGadgetId);
  }

  /**
   * Updates the backpack gadgets table with an update badge context.
   */
  public void updateBackpackGadget(GadgetContext context) {
    Integer backpackGadgetId = backpackGadgetId(context).orElse(null);

    if (backpackGadgetId == null) {
      return;
    }

    Magma.get().database().getBackpackGadgets()
        .updateContext(backpackGadgetId, context.serialize());
  }
}
