package net.avicus.magma.database.table.impl;

import java.util.Optional;
import net.avicus.magma.database.model.impl.IPBan;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.Table;

public class IPBanTable extends Table<IPBan> {

  public IPBanTable(Database database, String name, Class<IPBan> model) {
    super(database, name, model);
  }

  public Optional<IPBan> getByIp(String ip) {
    return select().where("ip", ip).execute().stream().findFirst();
  }
}
