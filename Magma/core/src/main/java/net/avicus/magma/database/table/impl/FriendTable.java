package net.avicus.magma.database.table.impl;

import java.util.List;
import java.util.Optional;
import net.avicus.magma.database.model.impl.Friend;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;
import net.avicus.quest.query.Filter;

public class FriendTable extends Table<Friend> {

  public FriendTable(Database database, String name, Class<Friend> model) {
    super(database, name, model);
  }

  public List<Friend> findByUser(int userId) {
    return select().where("user_id", userId).execute();
  }

  public boolean isFriendship(int user, int friend) {
    return findByAssociation(user, friend).map(Friend::isAccepted).orElse(false);
  }

  public Optional<Friend> findByAssociation(int user, int friend) {
    List<Friend> result = select().where("user_id", user).where("friend_id", friend).execute();
    if (result.size() == 0) {
      return Optional.empty();
    }
    return Optional.of(result.get(0));
  }

  /**
   * Delete any friendships between t (user_id and friend_id).
   */
  public void destroyAllAssociations(int user, int friend) {
    Filter friender = new Filter("user_id", user).and("friend_id", friend);
    Filter friended = new Filter("user_id", friend).and("friend_id", user);

    Filter filter = new Filter().or(friender).or(friended);

    delete().where(filter).execute();
  }
}
