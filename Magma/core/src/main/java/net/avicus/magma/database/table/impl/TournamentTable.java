package net.avicus.magma.database.table.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.util.concurrent.atomic.AtomicBoolean;
import net.avicus.magma.database.model.impl.Team;
import net.avicus.magma.database.model.impl.Tournament;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;

public class TournamentTable extends Table<Tournament> {

  private static final JsonParser parser = new JsonParser();

  public TournamentTable(Database database, String name, Class<Tournament> model) {
    super(database, name, model);
  }

  public boolean isTeamPlaying(Tournament tournament, Team team) {
    return !getDatabase().select("registrations").where("tournament_id", tournament.getId())
        .where("team_id", team.getId()).where("status", 1).execute().isEmpty();
  }

  public boolean isUserPlaying(Tournament tournament, Team team, int userId) {
    AtomicBoolean registered = new AtomicBoolean(false);
    getDatabase().select("registrations").where("tournament_id", tournament.getId())
        .where("team_id", team.getId()).where("status", 1).execute().forEach(r -> {
      JsonArray array = parser.parse(r.getString("user_data")).getAsJsonArray();
      array.forEach(e -> {
        if (e.isJsonArray()) {
          JsonArray inner = e.getAsJsonArray();
          int user = inner.get(0).getAsInt();
          int accepted = inner.get(1).getAsInt();
          if (user == userId) {
            registered.set(accepted == 1);
          }
        }
      });
    });
    return registered.get();
  }
}
