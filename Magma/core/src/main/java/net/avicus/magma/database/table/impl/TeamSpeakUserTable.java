package net.avicus.magma.database.table.impl;

import java.util.List;
import java.util.Optional;
import net.avicus.magma.database.model.impl.TeamSpeakUser;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.ModelList;
import net.avicus.quest.model.Table;

public class TeamSpeakUserTable extends Table<TeamSpeakUser> {

  public TeamSpeakUserTable(Database database, String name, Class<TeamSpeakUser> model) {
    super(database, name, model);
  }

  /**
   * Find all teamspeak clients a user has registered with.
   * <p>
   * Multiple results are possible due to the allowance
   * of multiple TS identities for one user.
   *
   * @param userId The user's id.
   * @return The teamspeak users.
   */
  public List<TeamSpeakUser> findByUser(int userId) {
    return select().where("user_id", userId).execute();
  }

  /**
   * Find a user based on a teamspeak client.
   * <p>
   * Due to old teamspeak clients/user deletion,
   * some may not be related to existent users.
   *
   * @param clientId The client's teamspeak database id.
   */
  public Optional<TeamSpeakUser> findByClient(int clientId) {
    Optional<TeamSpeakUser> result = Optional.empty();

    ModelList<TeamSpeakUser> select = select().where("client_id", clientId).execute();

    if (!select.isEmpty()) {
      result = Optional.of(select.last());
    }

    return result;
  }
}
