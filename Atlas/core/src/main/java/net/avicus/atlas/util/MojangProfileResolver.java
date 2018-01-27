package net.avicus.atlas.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import org.javalite.http.Get;
import org.javalite.http.Http;

/**
 * A profile resolver that uses Mojang's API.
 */
public class MojangProfileResolver implements MinecraftProfileResolver {

  private HashMap<UUID, String> resolved = new HashMap<>();

  @Override
  public Optional<String> username(UUID uuid) {
    if (resolved.containsKey(uuid)) {
      return Optional.of(resolved.get(uuid));
    }

    try {
      Get request = Http.get(String
          .format("https://sessionserver.mojang.com/session/minecraft/profile/%s",
              uuid.toString().replace("-", "")));
      request.header("User-Agent", "Java");

      JsonParser parser = new JsonParser();
      JsonObject json = parser.parse(request.text()).getAsJsonObject();

      String name = json.get("name").getAsString();

      resolved.putIfAbsent(uuid, name);
      return Optional.of(name);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<UUID> uuid(String username) {
    // Todo:
    return Optional.empty();
  }
}
