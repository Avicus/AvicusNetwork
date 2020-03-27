package net.avicus.magma.api.service.mock;

import net.avicus.magma.api.service.PresentsService;
import net.avicus.magma.database.model.impl.User;
import org.apache.commons.lang3.tuple.Pair;

public class MockPresentsService implements PresentsService {

  @Override
  public Pair<Boolean, String> find(User who, String family, String slug) {
    return Pair.of(false, "Presents are disabled");
  }
}
