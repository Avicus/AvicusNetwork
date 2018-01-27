package net.avicus.hook.backend.buycraft;

import java.util.Map;
import net.avicus.magma.database.model.impl.User;

public interface BuycraftPackage {

  void execute(Status status, User user, Map<String, String> variables);

  enum Status {
    INITIAL,
    RENEWAL,
    EXPIRY,
    CHARGEBACK,
    REFUND
  }
}
