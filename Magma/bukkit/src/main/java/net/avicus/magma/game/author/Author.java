package net.avicus.magma.game.author;

import java.net.URL;
import java.util.Optional;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public interface Author {

  /**
   * The name of this author, displayed to players during the match.
   */
  String getName();

  /**
   * A brief description of this author's role in creating the map.
   */
  Optional<String> getRole();

  /**
   * A link to this author's work to learn more about them.
   */
  Optional<URL> getPromo();

  default Optional<Localizable> getPromoLink() {
    if (!getPromo().isPresent()) {
      return Optional.empty();
    }
    Localizable localizable = new UnlocalizedText(getPromo().get().toString());
    if (getPromo().isPresent()) {
      localizable.style().click(new ClickEvent(Action.OPEN_URL, getPromo().get().toString()));
    }
    return Optional.of(localizable);
  }
}
