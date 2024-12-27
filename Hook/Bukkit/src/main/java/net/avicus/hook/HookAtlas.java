package net.avicus.hook;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.util.AtlasBridge;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.rtp.RemoteTeleports;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.network.user.rank.BukkitRank;
import net.avicus.magma.network.user.rank.Ranks;
import org.bukkit.entity.Player;

public class HookAtlas {

  public HookAtlas() {
    Atlas.get().setBridge(new AtlasBridge.SimpleAtlasBridge() {
      @Nullable
      @Override
      public String getServerName() {
        return Hook.server().getName();
      }

      @Nonnull
      @Override
      public Optional<String> getUsername(UUID uuid) {
        @Nullable final User found = Hook.database().getUsers().findByUuid(uuid).orElse(null);
        if (found == null) {
          return super.getUsername(uuid);
        }
        return Optional.of(found.getName());
      }

      @Nonnull
      @Override
      public Optional<UUID> getUniqueId(String name) {
        @Nullable final User found = Hook.database().getUsers().findByName(name).orElse(null);
        if (found == null) {
          return super.getUniqueId(name);
        }
        return Optional.of(found.getUniqueId());
      }

      @Nonnull
      @Override
      public String displayName(Match match, Player viewer, Player player) {
        final String base = super.displayName(match, viewer, player);

        User user = Users.user(player);
        List<BukkitRank> ranks = Ranks.get(user, true);
        Collections.reverse(ranks);

        UnlocalizedText text = Users.getPrefix(user, ranks);

        return (text == null ? "" : text.render(viewer).toLegacyText()) + base;
      }
    });
    RemoteTeleports.participationDelegate = new RemoteTeleports.ParticipationDelegate() {
      @Override
      public boolean isParticipating(Player viewer) {
        Match match = Atlas.getMatch();
        return match != null && !match.getRequiredModule(GroupsModule.class).isObserving(viewer);
      }
    };
  }
}
