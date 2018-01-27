package net.avicus.atlas.sets.competitve.objectives.actions.destroyable.base;

import static net.avicus.atlas.module.stats.action.ScoreUtils.getNearbyPlayers;

import com.google.common.util.concurrent.AtomicDouble;
import java.util.HashMap;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableEventInfo;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableObjective;
import net.avicus.magma.util.properties.ToolUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface DestroyableAction {

  DestroyableObjective getDestroyable();

  DestroyableEventInfo getInfo();

  default Pair<StringBuilder, Double> getBreakScore(ItemStack tool, Material broken,
      Player breaker) {
    Pair<StringBuilder, Double> res = MutablePair
        .of(new StringBuilder("Destroyable Break Data: "), 0.0);
    long breakDurr = ToolUtils.getBreakingDuration(broken, tool, breaker);
    res.getKey().append("breakDurr=" + breakDurr + " ");

    GroupsModule module = Atlas.getMatch().getRequiredModule(GroupsModule.class);

    Pair<StringBuilder, HashMap<Integer, AtomicDouble>> nearby = getNearbyPlayers(
        breaker.getLocation(), module.getCompetitorOf(breaker).get(), module);
    double in5 = nearby.getValue().get(5).get();
    double in10 = nearby.getValue().get(10).get();
    double in15 = nearby.getValue().get(15).get();
    double in20 = nearby.getValue().get(20).get();

    res.getKey().append(
        "in5=" + in5 + " in10=" + in10 + " in15=" + in15 + " in20=" + in20 + " data=" + nearby
            .getKey().toString());

    res.setValue(6.3 + (breakDurr * .42) + (in5 * .6) + (in10 * .43) + (in15 * .25) + (in20 * .13));

    res.getKey().append("score=" + res.getValue());

    return res;
  }
}
