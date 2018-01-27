package net.avicus.atlas.util;

import java.util.Optional;
import java.util.UUID;

public interface MinecraftProfileResolver {

  Optional<String> username(UUID uuid);

  Optional<UUID> uuid(String username);
}
