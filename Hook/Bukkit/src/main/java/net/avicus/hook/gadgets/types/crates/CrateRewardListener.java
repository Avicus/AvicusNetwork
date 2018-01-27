package net.avicus.hook.gadgets.types.crates;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import net.avicus.atlas.event.match.MatchCompleteEvent;
import net.avicus.compendium.WeightedRandomizer;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.Gadgets;
import net.avicus.magma.module.gadgets.crates.CrateGadget;
import net.avicus.magma.module.gadgets.crates.TypeManager;
import net.avicus.magma.network.user.Users;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CrateRewardListener implements Listener {

  private static CrateGadget ALPHA = new CrateGadget(TypeManager.getType("alpha"));
  private static CrateGadget BETA = new CrateGadget(TypeManager.getType("beta"));
  private static CrateGadget GAMMA = new CrateGadget(TypeManager.getType("gamma"));

  private static WeightedRandomizer<CrateGadget> crates = new WeightedRandomizer<>();

  static {
    // 10 of each
    for (int i = 0; i < 10; i++) {
      crates.set(ALPHA, 5);
      crates.set(BETA, 3);
      crates.set(GAMMA, 1);
    }
  }

  @EventHandler
  public void onComplete(MatchCompleteEvent event) {
    List<Player> participants = event.getCompetitors().stream()
        .flatMap(competitor -> competitor.getPlayers().stream()).collect(Collectors.toList());
    if (participants.size() < 5) {
      return;
    }

    int count = Math.min(Math.max(1, participants.size() / 10), 15);

    List<CrateGadget> gifts = crates.next(count);

    WeightedRandomizer<Player> playerSelector = new WeightedRandomizer<>();
    participants.forEach(player -> playerSelector.set(player, 1));

    gifts.forEach(crateGadget -> {
      User receiver = Users.user(playerSelector.next());
      Localizable crate = crateGadget.getName();
      crate.style().color(ChatColor.GOLD);
      crate.style().bold();
      Bukkit.broadcast(Messages.GENERIC_CRATE_REWARD
          .with(ChatColor.AQUA, crate, Users.getLocalizedDisplay(receiver)));
      HookTask.of(() -> Magma.get().getMm().get(Gadgets.class)
          .createBackpackGadget(receiver, crateGadget.defaultContext(), true, new Date()))
          .nowAsync();
    });
  }
}
