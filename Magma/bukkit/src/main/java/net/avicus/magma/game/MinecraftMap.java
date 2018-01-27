package net.avicus.magma.game;

import java.util.List;
import net.avicus.magma.game.author.Author;
import net.avicus.magma.util.Version;

public interface MinecraftMap {

  Version getSpecification();

  String getSlug();

  String getName();

  Version getVersion();

  List<Author> getAuthors();

  List<Author> getContributors();
}
