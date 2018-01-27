package net.avicus.magma.database.table.impl;

import java.util.Date;
import java.util.List;
import net.avicus.magma.database.model.impl.RankMember;
import net.avicus.magma.database.model.impl.User;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;

public class RankMemberTable extends Table<RankMember> {

  public RankMemberTable(Database database, String name, Class<RankMember> model) {
    super(database, name, model);
  }

  public void delete(RankMember membership) {
    delete().where("id", membership.getId()).execute();
  }

  public void setExpiration(RankMember membership, Date date) {
    update().where("id", membership.getId()).set("expires_at", date).execute();
  }

  public List<RankMember> findByUser(User user) {
    return select().where("member_id", user.getId()).execute();
  }
}
