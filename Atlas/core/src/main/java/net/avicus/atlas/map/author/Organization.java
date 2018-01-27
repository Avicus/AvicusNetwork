package net.avicus.atlas.map.author;

import java.net.URL;
import java.util.Optional;
import lombok.ToString;
import net.avicus.magma.game.author.Author;

@ToString
public class Organization implements Author {

  private final String name;
  private final Optional<String> role;
  private final Optional<URL> promo;

  public Organization(String name, Optional<String> role, Optional<URL> promo) {
    this.name = name;
    this.role = role;
    this.promo = promo;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Optional<String> getRole() {
    return this.role;
  }

  @Override
  public Optional<URL> getPromo() {
    return this.promo;
  }
}
