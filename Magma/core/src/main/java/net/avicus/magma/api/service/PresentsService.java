package net.avicus.magma.api.service;

import net.avicus.magma.database.model.impl.User;
import org.apache.commons.lang3.tuple.Pair;

public interface PresentsService {

  Pair<Boolean, String> find(User who, String family, String slug);
}
