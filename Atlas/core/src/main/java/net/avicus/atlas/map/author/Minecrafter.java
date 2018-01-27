package net.avicus.atlas.map.author;

import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.Atlas;
import net.avicus.magma.game.author.Author;

@ToString
public class Minecrafter implements Author {

  @Getter
  private final UUID uuid;
  private final Optional<String> role;
  private final Optional<URL> promo;
  private String username;

  public Minecrafter(String uuid, Optional<String> role, Optional<URL> promo) {
    uuid = uuid.replace("-", "");
    uuid = uuid.substring(0, 8) + "-" +
        uuid.substring(8, 12) + "-" +
        uuid.substring(12, 16) + "-" +
        uuid.substring(16, 20) + "-" +
        uuid.substring(20, 32);
    this.uuid = UUID.fromString(uuid);
    this.role = role;
    this.promo = promo;
  }

  @Override
  public String getName() {
    if (this.username == null) {
      this.username = fetchUsername();
    }
    return this.username;
  }

  @Override
  public Optional<String> getRole() {
    return this.role;
  }

  @Override
  public Optional<URL> getPromo() {
    return this.promo;
  }

  private String fetchUsername() {
    return Atlas.get().getBridge().getUsername(this.uuid).orElse("N/A");
  }
}
