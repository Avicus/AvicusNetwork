package net.avicus.atlas.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.javalite.http.Get;
import org.javalite.http.Http;

public interface AtlasBridge {

  @Nullable
  String getServerName();

  @Nonnull
  Optional<String> getUsername(UUID uuid);

  @Nonnull
  Optional<UUID> getUniqueId(String username);

  @Nonnull
  String displayName(Match match, Player viewer, Player player);

  class SimpleAtlasBridge implements AtlasBridge {

    private static final JsonParser JSON_PARSER = new JsonParser();
    private final Map<UUID, String> resolved = new HashMap<>();

    @Nullable
    @Override
    public String getServerName() {
      return null;
    }

    @Nonnull
    @Override
    public Optional<String> getUsername(UUID uuid) {
      if (this.resolved.containsKey(uuid)) {
        return Optional.of(this.resolved.get(uuid));
      }

      try {
        final Get request = Http.get(String
            .format("https://sessionserver.mojang.com/session/minecraft/profile/%s",
                uuid.toString().replace("-", "")))
            .header("User-Agent", "Java");
        final JsonObject json = JSON_PARSER.parse(request.text()).getAsJsonObject();
        final String name = json.get("name").getAsString();

        this.resolved.putIfAbsent(uuid, name);
        return Optional.of(name);
      } catch (Exception e) {
        return Optional.empty();
      }
    }

    @Nonnull
    @Override
    public Optional<UUID> getUniqueId(String username) {
      return Optional.empty();
    }

    @Nonnull
    @Override
    public String displayName(Match match, Player viewer, Player player) {
      final Group group = match.getRequiredModule(GroupsModule.class).getGroup(player);
      final ChatColor color = match.getRequiredModule(GroupsModule.class).getCompetitorOf(player)
          .map(Competitor::getChatColor).orElse(group.getChatColor());
      final String bold = viewer.equals(player) ? ChatColor.BOLD.toString() : "";
      return color + bold + player.getName() + ChatColor.RESET;
    }
  }
}
