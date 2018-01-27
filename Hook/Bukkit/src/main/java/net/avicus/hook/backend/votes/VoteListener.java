package net.avicus.hook.backend.votes;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import java.util.Date;
import net.avicus.hook.Hook;
import net.avicus.hook.utils.HookTask;
import net.avicus.magma.Magma;
import net.avicus.magma.NetworkIdentification;
import net.avicus.magma.announce.AnnounceMessageHandler;
import net.avicus.magma.module.gadgets.Gadgets;
import net.avicus.magma.module.gadgets.crates.CrateGadget;
import net.avicus.magma.module.gadgets.crates.KeyGadget;
import net.avicus.magma.module.gadgets.crates.TypeManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VoteListener implements Listener {

  @EventHandler
  public void onVote(VotifierEvent event) {
    HookTask.of(() -> {
      if (Hook.database() != null) {
        Vote vote = event.getVote();
        Hook.database().getUsers().findByName(vote.getUsername()).ifPresent(u -> {
          TextComponent has = new TextComponent(
              " has just voted and might win some exclusive rewards! Click here to vote!");
          TextComponent who = new TextComponent(u.getName());
          who.setColor(ChatColor.GOLD);
          has.setColor(ChatColor.GREEN);
          TextComponent message = new TextComponent(who, has);
          message
              .setClickEvent(new ClickEvent(Action.OPEN_URL, NetworkIdentification.URL + "/vote"));

          Hook.database().getVotes().insert(
              new net.avicus.magma.database.model.impl.Vote(u.getId(), vote.getServiceName(),
                  new Date())).execute();

          Hook.redis()
              .publish(new AnnounceMessageHandler.AnnounceMessage(new BaseComponent[]{message},
                  AnnounceMessageHandler.AnnounceType.MESSAGE, Hook.server()));

          Hook.database().getReceivers()
              .give(Hook.database().getAchievements().getOrCreate("vote"), u,
                  Hook.database().getAchievements(), (us, a) -> {
                  });

          if (Magma.get().database().getVotes().votesToday(u.getId()) > 4) {
            Magma.get().getMm().get(Gadgets.class).createBackpackGadget(u, new CrateGadget(
                TypeManager.getType("vote")).defaultContext(), false, new Date());

            Magma.get().getMm().get(Gadgets.class)
                .createBackpackGadget(u,
                    new KeyGadget(TypeManager.getType("vote")).defaultContext(),
                    false, new Date());
          }
        });
      }
    }).nowAsync();
  }

}
