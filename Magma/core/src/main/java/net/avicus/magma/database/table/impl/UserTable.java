package net.avicus.magma.database.table.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.avicus.magma.database.model.impl.User;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.ModelList;
import net.avicus.quest.model.Table;
import net.avicus.quest.query.Filter;
import net.avicus.quest.query.Operator;

public class UserTable extends Table<User> {

  public UserTable(Database database, String name, Class<User> model) {
    super(database, name, model);
  }

  public Optional<User> findByName(String name) {
    ModelList<User> list = select().where("username", name).order("created_at", "DESC").limit(1)
        .execute();
    if (list.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(list.first());
  }

  public Optional<User> findByUuid(UUID uuid) {
    return findByUuid(uuid.toString());
  }

  public Optional<User> findByDiscord(long discordId) {
    return Optional.ofNullable(select().where("discord_id", discordId).limit(1).execute().first());
  }

  public Optional<User> findByUuid(String uuid) {
    ModelList<User> list = select().where("uuid", uuid.replace("-", "")).limit(1).execute();
    if (list.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(list.first());
  }

  public Optional<User> findById(int id) {
    ModelList<User> list = select().where("id", id).limit(1).execute();
    if (list.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(list.first());
  }

  public boolean isRegistered(User user) {
    return !select().where("id", user.getId())
        .where(new Filter("password_secure", null, Operator.NOT_EQUAL)).execute().isEmpty();
  }

  public boolean registerProfile(UUID uuid, String verifyKey) {
    User user = findByUuid(uuid).orElse(null);
    if (user == null) {
      return false;
    }

    String correctKey = user.getVerifyKey();
    if (!Objects.equals(correctKey, verifyKey)) {
      return false;
    }

    update().set("verify_key_success", true).set("verify_key", null).where("id", user.getId())
        .execute();
    return true;
  }
}
