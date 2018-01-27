package net.avicus.magma.module.gadgets.ranks;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Rank;
import net.avicus.magma.database.model.impl.RankMember;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.EmptyGadgetContext;
import net.avicus.magma.module.gadgets.GadgetManager;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.util.MagmaTask;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RankManager implements GadgetManager<RankGadget, EmptyGadgetContext<RankGadget>> {

  public static final RankManager INSTANCE = new RankManager();
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private RankManager() {

  }

  @Override
  public String getType() {
    return "rank";
  }

  @Override
  public void init() {
    // Nothing fancy to do
  }

  @Override
  public void onAsyncLoad(User user, EmptyGadgetContext<RankGadget> context) {
  }

  @Override
  public void onAsyncUnload(User user, EmptyGadgetContext<RankGadget> context) {
    MagmaTask.of(() -> {
      Magma.get().database().getRankMembers().insert(
          new RankMember(user.getId(), context.getGadget().getRank().getId(),
              context.getGadget().getExpires().orElse(null))).execute();
      Users.player(user).ifPresent(p -> p.sendMessage(MagmaTranslations.GUI_RANK_RECEIVED.with(
          ChatColor.GREEN, new UnlocalizedText(context.getGadget().getRank().getName()))));
    }).nowAsync();
  }

  @Override
  public void onUse(Player player, EmptyGadgetContext<RankGadget> context) {

  }

  @Override
  public RankGadget deserializeGadget(JsonObject json) {
    int rank = json.get("rank").getAsInt();
    Optional<Date> expire = Optional.empty();
    if (json.has("expires")) {
      String expires = json.get("expires").getAsString();
      try {
        expire = Optional.of(DATE_FORMAT.parse(expires));
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    Optional<Rank> found = Magma.get().database().getRanks().findById(rank);
    if (found.isPresent()) {
      return new RankGadget(found.get(), expire);
    }

    throw new RuntimeException("Rank not found for ID " + rank);
  }
}
