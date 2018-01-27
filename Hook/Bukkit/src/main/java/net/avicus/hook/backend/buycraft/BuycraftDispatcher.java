package net.avicus.hook.backend.buycraft;

import com.google.common.base.Splitter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import net.avicus.hook.Hook;
import net.avicus.hook.backend.buycraft.BuycraftPackage.Status;
import net.avicus.hook.backend.buycraft.packages.AnnouncePackage;
import net.avicus.hook.backend.buycraft.packages.BadgePackage;
import net.avicus.hook.backend.buycraft.packages.GadgetPackage;
import net.avicus.hook.backend.buycraft.packages.RankPackage;
import net.avicus.hook.gadgets.types.badge.BadgeSymbol;
import net.avicus.hook.gadgets.types.map.setnext.SetNextGadget;
import net.avicus.hook.gadgets.types.map.startvote.StartVoteGadget;
import net.avicus.hook.gadgets.types.statreset.StatResetGadget;
import net.avicus.magma.NetworkIdentification;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.Rank;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.crates.KeyGadget;
import net.avicus.magma.module.gadgets.crates.TypeManager;
import net.buycraft.plugin.platform.standalone.runner.CommandDispatcher;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.joda.time.Months;

public class BuycraftDispatcher implements CommandDispatcher {

  private final Database database;
  private final Logger log;
  private final Map<Integer, BuycraftPackage> items;
  int GENERIC_BC_ID = 0;

  public BuycraftDispatcher(Database database) {
    this.database = database;
    this.log = Hook.plugin().getLogger();
    this.items = generateItems();
  }

  public Map<Integer, BuycraftPackage> generateItems() {
    Map<Integer, BuycraftPackage> map = new HashMap<>();

    Rank gold = fetchRank("gold");
    Rank emerald = fetchRank("emerald");
    Rank diamond = fetchRank("diamond");

    {
      // Gold
      map.put(GENERIC_BC_ID, new RankPackage(this.database, gold, Months.ONE));

      // Emerald
      map.put(GENERIC_BC_ID, new RankPackage(this.database, emerald, Months.ONE));

      // Diamond
      map.put(GENERIC_BC_ID, new RankPackage(this.database, diamond, Months.ONE));
    }

    {
      // Gold
      map.put(GENERIC_BC_ID, new RankPackage(this.database, gold));

      // Emerald
      map.put(GENERIC_BC_ID, new RankPackage(this.database, emerald));

      // Diamond
      map.put(GENERIC_BC_ID, new RankPackage(this.database, diamond));
    }

    {
      // Alpha Keys
      if (TypeManager.hasType("alpha")) {
        map.put(GENERIC_BC_ID, new GadgetPackage(new KeyGadget(TypeManager.getType("alpha")), 5));
      }

      // Beta Keys
      if (TypeManager.hasType("beta")) {
        map.put(GENERIC_BC_ID, new GadgetPackage(new KeyGadget(TypeManager.getType("beta")), 5));
      }

      // Gamma Keys
      if (TypeManager.hasType("gamma")) {
        map.put(GENERIC_BC_ID, new GadgetPackage(new KeyGadget(TypeManager.getType("gamma")), 5));
      }
    }

    {
      // Smiley Badge
      map.put(GENERIC_BC_ID, new BadgePackage(BadgeSymbol.SMILEY));

      // Airplane Badge
      map.put(GENERIC_BC_ID, new BadgePackage(BadgeSymbol.AIRPLANE));

      // Trademark Badge
      map.put(GENERIC_BC_ID, new BadgePackage(BadgeSymbol.TRADEMARK));

      // Copyright Badge
      map.put(GENERIC_BC_ID, new BadgePackage(BadgeSymbol.COPYRIGHT));

      // Hot Beverage
      map.put(GENERIC_BC_ID, new BadgePackage(BadgeSymbol.HOT_BEVERAGE));
    }

    {
      // Stats Reset
      map.put(GENERIC_BC_ID, new GadgetPackage(new StatResetGadget(), 1));

      // 5 SNs
      map.put(GENERIC_BC_ID, new GadgetPackage(new SetNextGadget(), 5));

      // 5 Map Votes
      map.put(GENERIC_BC_ID, new GadgetPackage(new StartVoteGadget(), 5));
    }
    return map;
  }

  /**
   * Fetches a rank, throws an exception if it is not found!
   */
  private Rank fetchRank(String name) throws RuntimeException {
    try {
      return this.database.getRanks().findByName(name).get();
    } catch (Exception e) {
      throw new RuntimeException("Could not find rank \"" + name + "\"");
    }
  }

  @Override
  public void dispatchCommand(String cmd) {
    List<String> split = Splitter.on(";").splitToList(cmd);

    if (split.size() < 3) {
      this.log.severe("Unable to parse command: " + cmd);
      return;
    }

    try {
      String uuid = split.get(0);
      int packageId = Integer.parseInt(split.get(1));
      Status status = Status.valueOf(split.get(2).toUpperCase());
      Map<String, String> variables = new HashMap<>();

      for (int i = 3; i < split.size(); i++) {
        String[] keyValue = split.get(i).split(":");
        variables.put(keyValue[0], keyValue[1]);
      }

      User user = this.database.getUsers().findByUuid(uuid).orElse(null);
      if (user == null) {
        this.log.severe("User " + uuid + " not found, ignoring.");
        return;
      }

      BuycraftPackage item = this.items.get(packageId);

      if (packageId == GENERIC_BC_ID) {
        // have to special case this here so we can supply names.
        TextComponent name = new TextComponent(user.getName());
        name.setColor(ChatColor.DARK_AQUA);
        TextComponent message = new TextComponent(
            " has just donated to " + NetworkIdentification.NAME + "! You can donate too at ");
        message.setColor(ChatColor.GREEN);
        TextComponent link = new TextComponent(NetworkIdentification.URL + "/shop");
        link.setUnderlined(true);
        link.setColor(ChatColor.RED);

        item = new AnnouncePackage(name, message, link);

        database.getReceivers()
            .give(Hook.database().getAchievements().getOrCreate("donate"), user,
                database.getAchievements(), (u, a) -> {
                });
      }

      if (item == null) {
        throw new Exception("Unknown item: " + packageId);
      }

      this.log.info("Executing: " + item.getClass().getSimpleName() + " [" + status + "]: " + user);
      item.execute(status, user, variables);

    } catch (Exception e) {
      this.log.severe("Unable to parse command: " + cmd);
      throw new RuntimeException(e);
    }
  }
}
